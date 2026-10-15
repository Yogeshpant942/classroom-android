import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { getClassStudents } from '../../redux/sclassRelated/sclassHandle';
import {
    Box, Paper, Typography, Button, Checkbox, Table, TableBody,
    TableCell, TableContainer, TableHead, TableRow, TextField, CircularProgress
} from '@mui/material';
import axios from 'axios';
import Popup from '../../components/Popup';

const BulkAttendance = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { currentUser } = useSelector((state) => state.user);
    const { sclassStudents, loading } = useSelector((state) => state.sclass);

    const classID = currentUser.teachSclass?._id;
    const subjectID = currentUser.teachSubject?._id;

    const [date, setDate] = useState('');
    const [absentStudents, setAbsentStudents] = useState([]);
    const [absentRollNumbersInput, setAbsentRollNumbersInput] = useState('');
    const [submitting, setSubmitting] = useState(false);
    
    const [showPopup, setShowPopup] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        dispatch(getClassStudents(classID));
    }, [dispatch, classID]);

    const handleCheckboxChange = (studentId) => {
        setAbsentStudents((prev) => {
            if (prev.includes(studentId)) {
                return prev.filter(id => id !== studentId);
            } else {
                return [...prev, studentId];
            }
        });
    };

    const handleRollNumbersChange = (e) => {
        const val = e.target.value;
        setAbsentRollNumbersInput(val);
        
        // Parse the comma-separated roll numbers
        const rollNums = val.split(',').map(r => r.trim()).filter(r => r !== '');
        
        // Find corresponding student IDs
        if (sclassStudents) {
            const matchedStudentIds = sclassStudents
                .filter(student => rollNums.includes(String(student.rollNum)))
                .map(student => student._id);
            setAbsentStudents(matchedStudentIds);
        }
    };

    const submitBulkAttendance = async () => {
        if (!date) {
            setMessage("Please select a date first");
            setShowPopup(true);
            return;
        }

        setSubmitting(true);
        try {
            const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/BulkStudentAttendance`, {
                sclassName: classID,
                subName: subjectID,
                date,
                absentStudentIds: absentStudents
            });
            setMessage(res.data.message || "Attendance submitted successfully");
            setShowPopup(true);
        } catch (error) {
            setMessage("Failed to submit attendance");
            setShowPopup(true);
            console.error(error);
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
                Bulk Attendance
            </Typography>
            <Typography variant="subtitle1" gutterBottom>
                Select the date, then mark ONLY the students who are <b>ABSENT</b>.
            </Typography>

            <Box sx={{ mb: 3, display: 'flex', alignItems: 'center', gap: 2 }}>
                <TextField
                    label="Attendance Date"
                    type="date"
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    InputLabelProps={{ shrink: true }}
                    required
                />
                <Button 
                    variant="contained" 
                    color="primary" 
                    onClick={submitBulkAttendance}
                    disabled={submitting}
                >
                    {submitting ? <CircularProgress size={24} /> : "Submit Attendance"}
                </Button>
                <Button variant="outlined" onClick={() => navigate(-1)}>Back</Button>
            </Box>

            <Box sx={{ mb: 3 }}>
                <TextField
                    label="Or quickly enter absentee Roll Numbers (comma separated)"
                    placeholder="e.g. 101, 104, 110"
                    value={absentRollNumbersInput}
                    onChange={handleRollNumbersChange}
                    fullWidth
                    helperText="Typing here will automatically check the boxes below"
                />
            </Box>

            {loading ? (
                <CircularProgress />
            ) : (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Roll Number</TableCell>
                                <TableCell>Name</TableCell>
                                <TableCell>Is Absent?</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {sclassStudents && sclassStudents.map((student) => (
                                <TableRow key={student._id}>
                                    <TableCell>{student.rollNum}</TableCell>
                                    <TableCell>{student.name}</TableCell>
                                    <TableCell>
                                        <Checkbox
                                            checked={absentStudents.includes(student._id)}
                                            onChange={() => handleCheckboxChange(student._id)}
                                            color="error"
                                        />
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
            <Popup message={message} setShowPopup={setShowPopup} showPopup={showPopup} />
        </Box>
    );
};

export default BulkAttendance;
