#!/bin/sh
set -eu

if [ "$#" -lt 2 ]; then
  echo "Usage: $0 /path/to/neo4j-home /path/to/db-home [db-name] [neo4j.conf]"
  echo "Example: $0 /opt/neo4j-5.26.21 /opt/neo4j-5.26.21/data/databases neo4j /opt/neo4j-5.26.21/conf/neo4j.conf"
  exit 1
fi

NEO4J_HOME="$1"
DB_HOME="$2"
DB_NAME="${3:-neo4j}"
CONF_PATH="${4:-}"

ADMIN="$NEO4J_HOME/bin/neo4j-admin"
if [ ! -x "$ADMIN" ]; then
  echo "neo4j-admin not found or not executable at: $ADMIN"
  exit 1
fi

if [ ! -d "$DB_HOME/$DB_NAME" ]; then
  echo "Database directory not found: $DB_HOME/$DB_NAME"
  echo "Ensure the database directory exists under the Neo4j home data/databases folder."
  exit 1
fi

CONF_ARGS=""
if [ -n "$CONF_PATH" ]; then
  if [ ! -f "$CONF_PATH" ]; then
    echo "neo4j.conf not found: $CONF_PATH"
    exit 1
  fi
  CONF_ARGS="--additional-config $CONF_PATH"
fi

cat <<'NOTE'
This script runs Neo4j 5.x offline store migration for an existing database.
The database must be stopped before running the migrate command.
It does NOT support direct migration from Neo4j 2.x stores.
For Neo4j 2.x, migrate through supported intermediate LTS versions first.
NOTE

"$ADMIN" database migrate "$DB_NAME" $CONF_ARGS

echo "Migration complete for $DB_NAME (if no errors were reported)."
