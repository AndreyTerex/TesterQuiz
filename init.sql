CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    username text NOT NULL UNIQUE,
    password text NOT NULL,
    role text NOT NULL,
    version INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tests (
    id uuid PRIMARY KEY,
    title text NOT NULL,
    topic text NOT NULL,
    creator_id uuid REFERENCES users(id) NOT NULL,
    version INTEGER DEFAULT 0,
    deleted BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS questions (
    id uuid PRIMARY KEY,
    question_number INTEGER,
    question_text text NOT NULL,
    test_id uuid REFERENCES tests(id) NOT NULL,
    version INTEGER DEFAULT 0,
    deleted BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS answers (
    id uuid PRIMARY KEY,
    answer_text text NOT NULL,
    correct BOOLEAN NOT NULL,
    question_id uuid REFERENCES questions(id) NOT NULL,
    version INTEGER DEFAULT 0,
    deleted BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS results (
    id uuid PRIMARY KEY,
    user_id uuid REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    test_id uuid REFERENCES tests(id) NOT NULL,
    test_title text ,
    score INTEGER,
    date TIMESTAMP,
    version INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS answers_in_result (
    id uuid PRIMARY KEY,
    result_id uuid REFERENCES results(id) ON DELETE CASCADE NOT NULL,
    question_id uuid REFERENCES questions(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS answers_in_result_selected_answers (
    answers_in_result_id uuid REFERENCES answers_in_result(id) ON DELETE CASCADE,
    answer_id uuid REFERENCES answers(id),
    PRIMARY KEY (answers_in_result_id, answer_id)
);
