import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import {
    Box, Paper, Typography, Button, TextField, CircularProgress, 
    List, ListItem, ListItemText, Grid
} from '@mui/material';
import axios from 'axios';
import Popup from '../../components/Popup';

const StudentAssignments = () => {
    const { currentUser } = useSelector((state) => state.user);
    const studentID = currentUser._id;
    const classID = currentUser.sclassName?._id;

    const [assignments, setAssignments] = useState([]);
    const [loading, setLoading] = useState(false);
    
    const [selectedAssignment, setSelectedAssignment] = useState(null);
    const [content, setContent] = useState('');
    const [submitting, setSubmitting] = useState(false);
    
    const [showPopup, setShowPopup] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        if (classID) {
            fetchAssignments();
        }
    }, [classID]);

    const fetchAssignments = async () => {
        setLoading(true);
        try {
            const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/AssignmentsClass/${classID}`);
            setAssignments(res.data);
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmitAssignment = async () => {
        if (!content) {
            setMessage("Please enter your assignment content.");
            setShowPopup(true);
            return;
        }

        setSubmitting(true);
        try {
            await axios.post(`${process.env.REACT_APP_BASE_URL}/AssignmentSubmit`, {
                student: studentID,
                assignment: selectedAssignment._id,
                content: content
            });
            setMessage(`Assignment submitted successfully!`);
            setShowPopup(true);
            setSelectedAssignment(null);
            setContent('');
        } catch (error) {
            setMessage("Failed to submit assignment.");
            setShowPopup(true);
        } finally {
            setSubmitting(false);
        }
    };

    if (selectedAssignment) {
        return (
            <Box sx={{ p: 3 }}>
                <Typography variant="h4" gutterBottom>Submit Assignment: {selectedAssignment.title}</Typography>
                <Paper sx={{ p: 4, mb: 4, backgroundColor: 'rgba(30, 30, 46, 0.7)', backdropFilter: 'blur(10px)', color: '#fff' }}>
                    <Typography variant="subtitle1" gutterBottom color="rgba(255, 255, 255, 0.7)">
                        Description: {selectedAssignment.description}
                    </Typography>
                    
                    <Box sx={{ mt: 4 }}>
                        <TextField
                            label="Your Answer / Content"
                            multiline
                            rows={10}
                            fullWidth
                            variant="outlined"
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            sx={{ mb: 3 }}
                        />
                    </Box>

                    <Box sx={{ display: 'flex', gap: 2 }}>
                        <Button 
                            variant="contained" 
                            color="primary" 
                            onClick={handleSubmitAssignment}
                            disabled={submitting}
                        >
                            {submitting ? <CircularProgress size={24} /> : "Submit Assignment"}
                        </Button>
                        <Button variant="outlined" color="secondary" onClick={() => setSelectedAssignment(null)}>
                            Cancel
                        </Button>
                    </Box>
                </Paper>
                <Popup message={message} setShowPopup={setShowPopup} showPopup={showPopup} />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>Class Assignments</Typography>
            {loading ? <CircularProgress /> : (
                <List>
                    {assignments.map((assignment) => (
                        <Paper key={assignment._id} sx={{ mb: 2, backgroundColor: 'rgba(30, 30, 46, 0.7)', backdropFilter: 'blur(10px)' }}>
                            <ListItem>
                                <ListItemText 
                                    primary={<Typography variant="h6">{assignment.title}</Typography>} 
                                    secondary={<Typography color="rgba(255,255,255,0.6)">{assignment.description.substring(0, 50)}...</Typography>}
                                />
                                <Button variant="contained" color="secondary" onClick={() => setSelectedAssignment(assignment)}>
                                    View & Submit
                                </Button>
                            </ListItem>
                        </Paper>
                    ))}
                    {assignments.length === 0 && <Typography>No assignments available.</Typography>}
                </List>
            )}
            <Popup message={message} setShowPopup={setShowPopup} showPopup={showPopup} />
        </Box>
    );
};

export default StudentAssignments;
