CREATE DATABASE roblox_db;
USE roblox_db;

-- CREATE TABLE STATEMENTS
CREATE TABLE PLAYER (
    playerID        INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    joinDate        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    robuxBalance    DECIMAL(18,2) NOT NULL DEFAULT 0
);

CREATE TABLE GAME (
    gameID          INT AUTO_INCREMENT PRIMARY KEY,
    gameName        VARCHAR(255) NOT NULL,
    genre           VARCHAR(100),
    dateCreated     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activePlayers   INT NOT NULL DEFAULT 0
);

CREATE TABLE ITEM (
    itemID          INT AUTO_INCREMENT PRIMARY KEY,
    itemName        VARCHAR(255) NOT NULL,
    itemType        ENUM('STORE','GAME') NOT NULL,
    price           DECIMAL(18,2) NOT NULL,
    availability    INT NOT NULL DEFAULT 1,
    ownerGameID     INT NULL,
    FOREIGN KEY (ownerGameID) REFERENCES GAME(gameID),

    -- CHECK: GAME items must have an ownerGameID; STORE items must NOT
    CONSTRAINT chk_item_ownerGame
        CHECK (
            (itemType = 'GAME' AND ownerGameID IS NOT NULL) OR
            (itemType = 'STORE' AND ownerGameID IS NULL)
        )
);

CREATE TABLE RATE (
    rateID          INT AUTO_INCREMENT PRIMARY KEY,
    itemID          INT NULL,
    gameID          INT NULL,
    price           DECIMAL(18,2) NOT NULL,
    effectiveDate   DATE NOT NULL,
    FOREIGN KEY (itemID) REFERENCES ITEM(itemID),
    FOREIGN KEY (gameID) REFERENCES GAME(gameID),

    -- CHECK: Exactly one of itemID or gameID must be set
    CONSTRAINT chk_rate_one_reference
        CHECK (
            (itemID IS NOT NULL AND gameID IS NULL) OR
            (itemID IS NULL AND gameID IS NOT NULL)
        )
);

CREATE TABLE PURCHASE (
    purchaseID      INT AUTO_INCREMENT PRIMARY KEY,
    playerID        INT NOT NULL,
    itemID          INT NOT NULL,
    priceAtPurchase DECIMAL(18,2) NOT NULL,
    purchaseDate    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (playerID) REFERENCES PLAYER(playerID),
    FOREIGN KEY (itemID) REFERENCES ITEM(itemID)
);

CREATE TABLE PLAYER_ITEM (
    playerID        INT NOT NULL,
    itemID          INT NOT NULL,
    quantity        INT NOT NULL DEFAULT 1,
    ownedSince      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (playerID, itemID),
    FOREIGN KEY (playerID) REFERENCES PLAYER(playerID),
    FOREIGN KEY (itemID) REFERENCES ITEM(itemID)
);

CREATE TABLE SESSION_ACTIVITY (
    sessionID       INT AUTO_INCREMENT PRIMARY KEY,
    playerID        INT NOT NULL,
    gameID          INT NOT NULL,
    joinTime        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    exitTime        DATETIME NULL,
    status          ENUM('active','inactive') NOT NULL,
    FOREIGN KEY (playerID) REFERENCES PLAYER(playerID),
    FOREIGN KEY (gameID) REFERENCES GAME(gameID)
);

CREATE TABLE ITEM_USAGE (
    usageID         INT AUTO_INCREMENT PRIMARY KEY,
    playerID        INT NOT NULL,
    itemID          INT NOT NULL,
    gameID          INT NOT NULL,
    sessionID       INT NULL,
    usageDate       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    effectResult    TEXT,
    FOREIGN KEY (playerID) REFERENCES PLAYER(playerID),
    FOREIGN KEY (itemID) REFERENCES ITEM(itemID),
    FOREIGN KEY (gameID) REFERENCES GAME(gameID),
    FOREIGN KEY (sessionID) REFERENCES SESSION_ACTIVITY(sessionID)
);

CREATE TABLE ROBUX_TRANSACTION (
    transactionID   INT AUTO_INCREMENT PRIMARY KEY,
    playerID        INT NOT NULL,
    gameID          INT NULL,
    type            ENUM('earn','spend') NOT NULL,
    amount          DECIMAL(18,2) NOT NULL,
    rateAppliedID   INT NULL,
    transDate       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (playerID) REFERENCES PLAYER(playerID),
    FOREIGN KEY (gameID) REFERENCES GAME(gameID),
    FOREIGN KEY (rateAppliedID) REFERENCES RATE(rateID)
);

-- SAMPLE DATA COMMANDS
INSERT INTO PLAYER (username, email, robuxBalance) VALUES
('ronan', 'ronan@gmail.com', 1500),
('james', 'james@gmail.com', 500),
('emeka', 'emeka@gmail.com', 300),
('david', 'david@gmail.com', 1250);

INSERT INTO GAME (gameName, genre, activePlayers) VALUES
('Pixel Blade', 'Adventure', 0),
('RIVALS', 'Action', 0),
('Grass Cutting Simulator', 'Simulation', 0);

INSERT INTO ITEM (itemName, itemType, price, availability, ownerGameID) VALUES
('Robux Sword', 'STORE', 50, 99, NULL),
('Golden Axe', 'STORE', 120, 50, NULL),
('Sky Wings', 'GAME', 200, 10, 1),
('Battle Shield', 'GAME', 90, 20, 2);

INSERT INTO RATE (itemID, gameID, price, effectiveDate) VALUES
(1, NULL, 50, '2025-01-01'),
(2, NULL, 120, '2025-01-01'),
(NULL, 1, 15, '2025-01-01'),
(NULL, 2, 10, '2025-01-01');

INSERT INTO PURCHASE (playerID, itemID, priceAtPurchase) VALUES
(1, 1, 50),
(1, 3, 200),
(2, 4, 90),
(3, 2, 120);

INSERT INTO PLAYER_ITEM (playerID, itemID, quantity) VALUES
(1, 1, 1),
(1, 3, 1),
(2, 4, 1),
(3, 2, 1);

INSERT INTO SESSION_ACTIVITY (playerID, gameID, status) VALUES
(1, 1, 'active'),
(2, 2, 'active'),
(3, 3, 'inactive');

INSERT INTO ITEM_USAGE (playerID, itemID, gameID, sessionID, effectResult) VALUES
(1, 3, 1, 1, 'Flight Boost'),
(2, 4, 2, 2, 'Defense +20');

INSERT INTO ROBUX_TRANSACTION (playerID, type, amount, rateAppliedID) VALUES
(1, 'earn', 300, NULL),
(1, 'spend', 50, 1),
(2, 'spend', 90, 4);

-- REPORT GENERATION COMMANDS
-- PLAYER ACTIVITY REPORT
SELECT 
    p.playerID,
    p.username,
    COUNT(s.sessionID) AS totalSessions,
    SUM(
        CASE 
            WHEN s.exitTime IS NOT NULL 
                THEN TIMESTAMPDIFF(MINUTE, s.joinTime, s.exitTime)
            ELSE 0
        END
    ) AS totalMinutesPlayed,
    COUNT(DISTINCT s.gameID) AS uniqueGamesPlayed
FROM PLAYER p
LEFT JOIN SESSION_ACTIVITY s ON p.playerID = s.playerID
GROUP BY p.playerID, p.username
ORDER BY totalSessions DESC;

-- GAME ENGAGEMENT REPORT
SELECT
    g.gameID,
    g.gameName,
    COUNT(s.sessionID) AS totalSessions,
    COUNT(DISTINCT s.playerID) AS uniquePlayers,
    SUM(CASE WHEN s.status = 'active' THEN 1 ELSE 0 END) AS activeSessions
FROM GAME g
LEFT JOIN SESSION_ACTIVITY s ON g.gameID = s.gameID
GROUP BY g.gameID, g.gameName
ORDER BY uniquePlayers DESC;

-- ITEM USAGE REPORT
SELECT
    i.itemID,
    i.itemName,
    COUNT(u.usageID) AS timesUsed,
    COUNT(DISTINCT u.playerID) AS uniquePlayers,
    GROUP_CONCAT(DISTINCT p.username SEPARATOR ', ') AS playersWhoUsed
FROM ITEM i
LEFT JOIN ITEM_USAGE u ON i.itemID = u.itemID
LEFT JOIN PLAYER p ON u.playerID = p.playerID
GROUP BY i.itemID, i.itemName
ORDER BY timesUsed DESC;

-- REVENUE REPORT
SELECT
    p.playerID,
    p.username,
    SUM(CASE WHEN t.type = 'spend' THEN t.amount ELSE 0 END) AS totalSpent,
    SUM(CASE WHEN t.type = 'earn' THEN t.amount ELSE 0 END) AS totalEarned,
    (SUM(CASE WHEN t.type = 'earn' THEN t.amount ELSE 0 END)
     - SUM(CASE WHEN t.type = 'spend' THEN t.amount ELSE 0 END)) 
        AS netRobuxChange
FROM PLAYER p
LEFT JOIN ROBUX_TRANSACTION t ON p.playerID = t.playerID
GROUP BY p.playerID, p.username
ORDER BY totalSpent DESC;