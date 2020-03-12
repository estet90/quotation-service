CREATE TABLE elvls
(
    isin  VARCHAR(12) PRIMARY KEY,
    value NUMBER NOT NULL
);

CREATE TABLE history
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    isin VARCHAR(12) NOT NULL,
    bid  NUMBER,
    ask  NUMBER      NOT NULL,
    date DATETIME    NOT NULL
);