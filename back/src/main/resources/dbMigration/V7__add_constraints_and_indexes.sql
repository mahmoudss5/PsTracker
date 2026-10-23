-- Strengthen ownership and Codeforces data integrity, then support the
-- user/team/date access patterns used by the application.

-- These relationships are mandatory in the application. Fail the migration
-- with a useful message instead of silently changing or losing old data.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM teams WHERE coach_id IS NULL) THEN
        RAISE EXCEPTION 'Cannot make teams.coach_id NOT NULL: existing teams have no coach';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM submissions
        WHERE user_id IS NULL OR problem_id IS NULL OR created_at IS NULL
    ) THEN
        RAISE EXCEPTION 'Cannot make submission ownership columns NOT NULL: existing submissions are incomplete';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM team_messages
        WHERE sender_id IS NULL OR team_id IS NULL OR content IS NULL OR created_at IS NULL
    ) THEN
        RAISE EXCEPTION 'Cannot make team message ownership columns NOT NULL: existing messages are incomplete';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM contests
        WHERE user_id IS NULL OR contest_id IS NULL OR rating_update_time IS NULL
    ) THEN
        RAISE EXCEPTION 'Cannot make contest ownership columns NOT NULL: existing contests are incomplete';
    END IF;

    IF EXISTS (SELECT 1 FROM notifications WHERE user_id IS NULL) THEN
        RAISE EXCEPTION 'Cannot make notifications.user_id NOT NULL: existing notifications have no owner';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM materials
        WHERE team_id IS NULL
           OR file_type IS NULL
           OR s3_key IS NULL
           OR file_size IS NULL
           OR created_at IS NULL
    ) THEN
        RAISE EXCEPTION 'Cannot make material ownership and storage columns NOT NULL: existing materials are incomplete';
    END IF;
END $$;

ALTER TABLE teams
    ALTER COLUMN coach_id SET NOT NULL;

ALTER TABLE submissions
    ALTER COLUMN user_id SET NOT NULL,
    ALTER COLUMN problem_id SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE team_messages
    ALTER COLUMN sender_id SET NOT NULL,
    ALTER COLUMN team_id SET NOT NULL,
    ALTER COLUMN content SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE contests
    ALTER COLUMN user_id SET NOT NULL,
    ALTER COLUMN contest_id SET NOT NULL,
    ALTER COLUMN rating_update_time SET NOT NULL;

ALTER TABLE notifications
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE materials
    ALTER COLUMN team_id SET NOT NULL,
    ALTER COLUMN file_type SET NOT NULL,
    ALTER COLUMN s3_key SET NOT NULL,
    ALTER COLUMN file_size SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL;

-- A trainee can belong to one team because membership is represented by the
-- single users.trainee_team_id foreign key.
ALTER TABLE users
    ADD CONSTRAINT ck_users_role_valid
        CHECK (role IS NULL OR role IN ('Admin', 'Coach', 'Trainee')),
    ADD CONSTRAINT ck_trainee_team_requires_trainee
        CHECK (trainee_team_id IS NULL OR (role IS NOT NULL AND role = 'Trainee')),
    ADD CONSTRAINT ck_users_rate_non_negative
        CHECK (rate IS NULL OR rate >= 0),
    ADD CONSTRAINT ck_users_max_rate_non_negative
        CHECK (max_rate IS NULL OR max_rate >= 0);

-- Codeforces identity and numeric value checks.
ALTER TABLE submissions
    ADD CONSTRAINT ck_submissions_codeforces_id_positive
        CHECK (codeforces_submission_id IS NULL OR codeforces_submission_id > 0),
    ADD CONSTRAINT ck_submissions_time_non_negative
        CHECK (time_consumed_ms >= 0),
    ADD CONSTRAINT ck_submissions_memory_non_negative
        CHECK (memory_consumed_bytes >= 0);

ALTER TABLE problems
    ADD CONSTRAINT ck_problems_contest_id_positive
        CHECK (contest_id IS NULL OR contest_id > 0),
    ADD CONSTRAINT ck_problems_rating_non_negative
        CHECK (rating IS NULL OR rating >= 0);

ALTER TABLE contests
    ADD CONSTRAINT ck_contests_codeforces_id_positive
        CHECK (contest_id > 0),
    ADD CONSTRAINT ck_contests_rank_positive
        CHECK (rank > 0),
    ADD CONSTRAINT ck_contests_ratings_non_negative
        CHECK (old_rating >= 0 AND new_rating >= 0);

-- A user/contest and a material/object key must be unique. The problem index
-- is nullable for manually-created or older records, so only complete
-- Codeforces identities participate in this unique index.
ALTER TABLE contests
    ADD CONSTRAINT uq_contests_user_codeforces_id UNIQUE (user_id, contest_id);

ALTER TABLE materials
    ADD CONSTRAINT uq_materials_s3_key UNIQUE (s3_key);

CREATE UNIQUE INDEX ux_problems_codeforces_identity
    ON problems (contest_id, problem_index)
    WHERE contest_id IS NOT NULL AND problem_index IS NOT NULL;

-- Ownership and common user/team/date query indexes.
CREATE INDEX ix_users_trainee_team_id
    ON users (trainee_team_id);

CREATE INDEX ix_teams_coach_id
    ON teams (coach_id);

CREATE INDEX ix_submissions_user_created_at
    ON submissions (user_id, created_at DESC);

CREATE INDEX ix_submissions_problem_created_at
    ON submissions (problem_id, created_at DESC);

CREATE INDEX ix_team_messages_team_created_id
    ON team_messages (team_id, created_at ASC, id ASC);

CREATE INDEX ix_notifications_user_created_at
    ON notifications (user_id, created_at DESC);

CREATE INDEX ix_reports_user_created_at
    ON reports (user_id, created_at DESC);

CREATE INDEX ix_contests_user_rating_update_time
    ON contests (user_id, rating_update_time ASC);

CREATE INDEX ix_materials_team_created_at
    ON materials (team_id, created_at DESC);

CREATE INDEX ix_announcement_users_user_id
    ON announcement_users (user_id, announcement_id);

CREATE INDEX ix_announcement_teams_team_id
    ON announcement_teams (team_id, announcement_id);

CREATE INDEX ix_problem_tags_tag_id
    ON problem_tags (tag_id, problem_id);

CREATE INDEX ix_refresh_tokens_user_id
    ON refresh_tokens (user_id);
