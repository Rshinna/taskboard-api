CREATE TABLE tasks (
                       id          UUID          NOT NULL,
                       title       VARCHAR(255)  NOT NULL,
                       description VARCHAR(255),
                       status      VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
                       created_at  TIMESTAMP     NOT NULL,
                       updated_at  TIMESTAMP     NOT NULL,
                       user_id     UUID,
                       PRIMARY KEY (id),
                       CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id)
);