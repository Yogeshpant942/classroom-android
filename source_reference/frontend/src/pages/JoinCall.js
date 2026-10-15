import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Button, TextField, Typography, Container, Paper } from '@mui/material';

const JoinCall = ({ role }) => {
    const [roomID, setRoomID] = useState('');
    const navigate = useNavigate();

    const handleJoin = () => {
        if (roomID.trim()) {
            navigate(`/${role}/video-call/${roomID}`);
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Paper elevation={3} sx={{ padding: 4, display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: 8 }}>
                <Typography component="h1" variant="h5" sx={{ mb: 3 }}>
                    Join a Video Class
                </Typography>
                <Box component="form" noValidate sx={{ mt: 1, width: '100%' }}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="roomID"
                        label="Room ID / Class ID"
                        name="roomID"
                        autoFocus
                        value={roomID}
                        onChange={(e) => setRoomID(e.target.value)}
                    />
                    <Button
                        type="button"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                        onClick={handleJoin}
                    >
                        Join Call
                    </Button>
                </Box>
            </Paper>
        </Container>
    );
};

export default JoinCall;
