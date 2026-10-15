@echo off
echo Starting School Management System...

echo Starting Backend Server...
start "Backend Server" cmd /k "cd backend && npm start"

echo Starting Frontend Server...
start "Frontend Server" cmd /k "cd frontend && set BROWSER=none && npm start"

echo Both servers are starting up in separate windows!
echo It may take a minute for the React app to compile.
echo Open http://localhost:3000 in your browser if it doesn't open automatically.
