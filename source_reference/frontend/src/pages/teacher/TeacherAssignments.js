import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import {
    Box, Paper, Typography, Button, TextField, CircularProgress, 
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Collapse
} from '@mui/material';
import { KeyboardArrowDown, KeyboardArrowUp } from '@mui/icons-material';
import axios from 'axios';
import Popup from '../../components/Popup';

const TeacherAssignments = () => {
    const { currentUser } = useSelector((state) => state.user);

    const classID = currentUser.teachSclass?._id;
    const subjectID = currentUser.teachSubject?._id;
    const teacherID = currentUser._id;

    const [assignments, setAssignments] = useState([]);
    const [loading, setLoading] = useState(false);
    
    // New Assignment state
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    
    // Submissions state
    const [submissions, setSubmissions] = useState({});
    const [openStates, setOpenStates] = useState({});

    const [showPopup, setShowPopup] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        fetchAssignments();
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

    const handleCreateAssignment = async () => {
        if (!title || !description) {
            setMessage("Title and description are required");
            setShowPopup(true);
            return;
        }

        try {
            await axios.post(`${process.env.REACT_APP_BASE_URL}/AssignmentCreate`, {
                title,
                description,
                subject: subjectID,
                sclassName: classID,
                teacher: teacherID
            });
            setMessage("Assignment created successfully!");
            setShowPopup(true);
            setTitle('');
            setDescription('');
            fetchAssignments();
        } catch (error) {
            setMessage("Failed to create assignment");
            setShowPopup(true);
            console.error(error);
        }
    };

    const fetchSubmissions = async (assignmentId) => {
        try {
            const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/AssignmentSubmissions/${assignmentId}`);
            setSubmissions(prev => ({ ...prev, [assignmentId]: res.data }));
        } catch (error) {
            console.error(error);
        }
    };

    const handleOpen = (assignmentId) => {
        setOpenStates((prevState) => ({
            ...prevState,
            [assignmentId]: !prevState[assignmentId],
        }));
        if (!openStates[assignmentId]) {
            fetchSubmissions(assignmentId);
        }
    };

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
                Assignment Management
            </Typography>

            <Paper sx={{ p: 3, mb: 4, backgroundColor: 'rgba(30, 30, 46, 0.7)', backdropFilter: 'blur(10px)' }}>
                <Typography variant="h6" gutterBottom>Create New Assignment</Typography>
                
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mb: 3 }}>
                    <TextField 
                        label="Assignment Title" 
                        value={title} 
                        onChange={(e) => setTitle(e.target.value)} 
                        fullWidth 
                    />
                    <TextField 
                        label="Assignment Description" 
                        value={description} 
                        onChange={(e) => setDescription(e.target.value)} 
                        fullWidth 
                        multiline
                        rows={4}
                    />
                    <Button variant="contained" color="primary" onClick={handleCreateAssignment}>
                        Post Assignment
                    </Button>
                </Box>
            </Paper>

            <Typography variant="h5" gutterBottom>Existing Assignments</Typography>
            {loading ? <CircularProgress /> : (
                <TableContainer component={Paper} sx={{ backgroundColor: 'rgba(30, 30, 46, 0.7)', backdropFilter: 'blur(10px)' }}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Title</TableCell>
                                <TableCell>Description</TableCell>
                                <TableCell>Created At</TableCell>
                                <TableCell>Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {assignments.map((assignment) => (
                                <React.Fragment key={assignment._id}>
                                    <TableRow>
                                        <TableCell>{assignment.title}</TableCell>
                                        <TableCell>{assignment.description}</TableCell>
                                        <TableCell>{new Date(assignment.createdAt).toLocaleDateString()}</TableCell>
                                        <TableCell>
                                            <Button variant="outlined" onClick={() => handleOpen(assignment._id)}>
                                                {openStates[assignment._id] ? <KeyboardArrowUp /> : <KeyboardArrowDown />} Submissions
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                    <TableRow>
                                        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={4}>
                                            <Collapse in={openStates[assignment._id]} timeout="auto" unmountOnExit>
                                                <Box sx={{ margin: 2 }}>
                                                    <Typography variant="h6" gutterBottom component="div">
                                                        Student Submissions
                                                    </Typography>
                                                    <Table size="small">
                                                        <TableHead>
                                                            <TableRow>
                                                                <TableCell>Student</TableCell>
                                                                <TableCell>AI Summary</TableCell>
                                                                <TableCell>Plagiarism Status</TableCell>
                                                                <TableCell>Full Text</TableCell>
                                                            </TableRow>
                                                        </TableHead>
                                                        <TableBody>
                                                            {submissions[assignment._id]?.map((sub) => (
                                                                <TableRow key={sub._id}>
                                                                    <TableCell>{sub.student?.name} ({sub.student?.rollNum})</TableCell>
                                                                    <TableCell>{sub.summary}</TableCell>
                                                                    <TableCell>
                                                                        {sub.plagiarizedFrom && sub.plagiarizedFrom.length > 0 ? (
                                                                            <Typography color="error" fontWeight="bold">
                                                                                ⚠️ Flagged (Similar to: {sub.plagiarizedFrom.map(p => p.name).join(', ')})
                                                                            </Typography>
                                                                        ) : (
                                                                            <Typography color="success.main">Original</Typography>
                                                                        )}
                                                                    </TableCell>
                                                                    <TableCell>
                                                                        <Button size="small" onClick={() => {
                                                                            setMessage(sub.content);
                                                                            setShowPopup(true);
                                                                        }}>View Content</Button>
                                                                    </TableCell>
                                                                </TableRow>
                                                            ))}
                                                            {(!submissions[assignment._id] || submissions[assignment._id].length === 0) && (
                                                                <TableRow>
                                                                    <TableCell colSpan={4}>No submissions yet.</TableCell>
                                                                </TableRow>
                                                            )}
                                                        </TableBody>
                                                    </Table>
                                                </Box>
                                            </Collapse>
                                        </TableCell>
                                    </TableRow>
                                </React.Fragment>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            <Popup message={message} setShowPopup={setShowPopup} showPopup={showPopup} />
        </Box>
    );
};

export default TeacherAssignments;
