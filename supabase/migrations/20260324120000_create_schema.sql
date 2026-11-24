-- Copia para Supabase CLI / SQL Editor
-- Origen: backend/src/main/resources/db/migration/V1__create_schema.sql

CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(255) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE courses (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     VARCHAR(2000) NOT NULL,
    level           VARCHAR(100) NOT NULL,
    duration_hours  INTEGER NOT NULL,
    modality        VARCHAR(255) NOT NULL,
    rating          DOUBLE PRECISION NOT NULL,
    unit_count      INTEGER NOT NULL,
    category        VARCHAR(100) NOT NULL
);

CREATE TABLE activities (
    id               BIGSERIAL PRIMARY KEY,
    course_id        BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    unit_number      INTEGER NOT NULL,
    title            VARCHAR(255) NOT NULL,
    type             VARCHAR(50) NOT NULL,
    duration_minutes INTEGER NOT NULL,
    order_index      INTEGER NOT NULL,
    summary          VARCHAR(1500) NOT NULL
);

CREATE INDEX idx_activities_course_order ON activities(course_id, order_index);

CREATE TABLE enrollments (
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id          BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    status             VARCHAR(50) NOT NULL DEFAULT 'INSCRITO',
    progress_percent   INTEGER NOT NULL DEFAULT 0,
    last_activity_id   BIGINT REFERENCES activities(id) ON DELETE SET NULL,
    enrolled_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_accessed_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_enrollment_user_course UNIQUE (user_id, course_id)
);

CREATE INDEX idx_enrollments_user ON enrollments(user_id);

CREATE TABLE activity_progress (
    id               BIGSERIAL PRIMARY KEY,
    enrollment_id    BIGINT NOT NULL REFERENCES enrollments(id) ON DELETE CASCADE,
    activity_id      BIGINT NOT NULL REFERENCES activities(id) ON DELETE CASCADE,
    percent_watched  INTEGER NOT NULL DEFAULT 0,
    completed        BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_progress_enrollment_activity UNIQUE (enrollment_id, activity_id)
);

CREATE INDEX idx_progress_enrollment ON activity_progress(enrollment_id);

CREATE TABLE weekly_availability (
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    monday_minutes     INTEGER NOT NULL DEFAULT 0,
    tuesday_minutes    INTEGER NOT NULL DEFAULT 0,
    wednesday_minutes  INTEGER NOT NULL DEFAULT 0,
    thursday_minutes   INTEGER NOT NULL DEFAULT 0,
    friday_minutes     INTEGER NOT NULL DEFAULT 0,
    saturday_minutes   INTEGER NOT NULL DEFAULT 0,
    sunday_minutes     INTEGER NOT NULL DEFAULT 0,
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE study_plan_items (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    activity_id      BIGINT NOT NULL REFERENCES activities(id) ON DELETE CASCADE,
    day_label        VARCHAR(50) NOT NULL,
    duration_minutes INTEGER NOT NULL,
    order_index      INTEGER NOT NULL
);

CREATE INDEX idx_plan_items_user_order ON study_plan_items(user_id, order_index);

CREATE TABLE help_topics (
    id         BIGSERIAL PRIMARY KEY,
    course_id  BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    question   VARCHAR(255) NOT NULL,
    answer     VARCHAR(2000) NOT NULL,
    tags       VARCHAR(255) NOT NULL
);

CREATE INDEX idx_help_topics_course ON help_topics(course_id);

CREATE TABLE tutor_questions (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id         BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    activity_id       BIGINT REFERENCES activities(id) ON DELETE SET NULL,
    question          VARCHAR(2000) NOT NULL,
    private_question  BOOLEAN NOT NULL DEFAULT TRUE,
    status            VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tutor_questions_user ON tutor_questions(user_id, created_at DESC);
