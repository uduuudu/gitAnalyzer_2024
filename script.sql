
CREATE TABLE projects (
      id SERIAL PRIMARY KEY,
      name VARCHAR NOT NULL,
      url VARCHAR NOT NULL,
      analyze_branch VARCHAR NOT NULL,
      directory VARCHAR NOT NULL
);

CREATE TABLE authors (
     id SERIAL PRIMARY KEY,
     name VARCHAR NOT NULL
);

CREATE TABLE commits (
     id SERIAL PRIMARY KEY,
     project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE,
     author_id INTEGER REFERENCES authors(id),
     hash VARCHAR UNIQUE NOT NULL,
     commit_date TIMESTAMP NOT NULL,
     message TEXT NOT NULL
);

CREATE TABLE files (
   id SERIAL PRIMARY KEY,
   project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE,
   path VARCHAR NOT NULL,
   name VARCHAR NOT NULL
);

CREATE TABLE changes (
     id SERIAL PRIMARY KEY,
     commit_id INTEGER REFERENCES commits(id) ON DELETE CASCADE,
     file_id INTEGER REFERENCES files(id) ON DELETE CASCADE,
     change_type VARCHAR NOT NULL,
     lines_added INTEGER NOT NULL,
     lines_deleted INTEGER NOT NULL
);

-- Каскадное удаление для авторов через триггер
CREATE OR REPLACE FUNCTION delete_authors() RETURNS TRIGGER AS $$
BEGIN
DELETE FROM authors WHERE id = OLD.author_id AND NOT EXISTS (SELECT 1 FROM commits WHERE author_id = OLD.author_id);
RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER delete_authors_trigger
    AFTER DELETE ON commits
    FOR EACH ROW
    EXECUTE FUNCTION delete_authors();