import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import {
    Box, Paper, Typography, Button, TextField, CircularProgress, 
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Select, MenuItem, InputLabel, FormControl
} from '@mui/material';
import axios from 'axios';
import Popup from '../../components/Popup';

const LabGroupManagement = () => {
    const { currentUser } = useSelector((state) => state.user);
    const adminID = currentUser._id;

    const [labGroups, setLabGroups] = useState([]);
    const [classes, setClasses] = useState([]);
    const [students, setStudents] = useState([]);
    
    const [loading, setLoading] = useState(false);
    
    const [groupName, setGroupName] = useState('');
    const [selectedClass, setSelectedClass] = useState('');
    
    const [showPopup, setShowPopup] = useState(false);
    const [message, setMessage] = useState("");

    // Assignment state
    const [assignGroupId, setAssignGroupId] = useState('');
    const [assignStudentId, setAssignStudentId] = useState('');

    useEffect(() => {
        fetchClasses();
        // Since getStudents returns all students for the school
        fetchStudents();
    }, [adminID]);

    useEffect(() => {
        if (selectedClass) {
            fetchLabGroups(selectedClass);
        }
    }, [selectedClass]);

    const fetchClasses = async () => {
        try {
            const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/SclassList/${adminID}`);
            if (Array.isArray(res.data)) {
                setClasses(res.data);
            } else {
                setClasses([]);
            }
        } catch (error) {
            console.error(error);
        }
    };

    const fetchStudents = async () => {
        try {
            const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/Students/${adminID}`);
            if (Array.isArray(res.data)) {
                setStudents(res.data);
            }
        } catch (error) {
            console.error(error);
        }
    };

    const fetchLabGroups = async (classId) => {
        setLoading(true);
        try {
            const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/LabGroupsClass/${classId}`);
            if (Array.isArray(res.data)) {
                setLabGroups(res.data);
            } else {
                setLabGroups([]);
            }
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateLabGroup = async () => {
        if (!groupName || !selectedClass) {
            setMessage("Group name and class are required");
            setShowPopup(true);
            return;
        }

        try {
            await axios.post(`${process.env.REACT_APP_BASE_URL}/LabGroupCreate`, {
                groupName,
                sclassName: selectedClass,
                school: adminID
            });
            setMessage("Lab Group created successfully!");
            setShowPopup(true);
            setGroupName('');
            fetchLabGroups(selectedClass);
        } catch (error) {
            setMessage("Failed to create Lab Group");
            setShowPopup(true);
            console.error(error);
        }
    };

    const handleAssignStudent = async () => {
        if (!assignGroupId || !assignStudentId) {
            setMessage("Group and Student are required");
            setShowPopup(true);
            return;
        }
        
        try {
            await axios.post(`${process.env.REACT_APP_BASE_URL}/LabGroupAssign`, {
                labGroupId: assignGroupId,
                studentId: assignStudentId
            });
            setMessage("Student assigned to Lab Group successfully!");
            setShowPopup(true);
            setAssignGroupId('');
            setAssignStudentId('');
            fetchLabGroups(selectedClass);
        } catch (error) {
            setMessage("Failed to assign student");
            setShowPopup(true);
            console.error(error);
        }
    };

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
                Lab Group Management
            </Typography>

            <Paper sx={{ p: 3, mb: 4, backgroundColor: 'rgba(30, 30, 46, 0.7)', backdropFilter: 'blur(10px)', color: '#fff' }}>
                <Typography variant="h6" gutterBottom>Create New Lab Group</Typography>
                
                <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
                    <FormControl sx={{ minWidth: 200 }}>
                        <InputLabel>Select Class</InputLabel>
                        <Select
                            value={selectedClass}
                            label="Select Class"
                            onChange={(e) => setSelectedClass(e.target.value)}
                        >
                            {classes.map((sclass) => (
                                <MenuItem key={sclass._id} value={sclass._id}>
                                    {sclass.sclassName}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <TextField 
                        label="Group Name (e.g. Group A)" 
                        value={groupName} 
                        onChange={(e) => setGroupName(e.target.value)} 
                        sx={{ flexGrow: 1 }}
                    />
                    
                    <Button variant="contained" color="primary" onClick={handleCreateLabGroup}>
                        Create
                    </Button>
                </Box>
            </Paper>

            {selectedClass && (
                <Paper sx={{ p: 3, mb: 4, backgroundColor: 'rgba(30, 30, 46, 0.7)', backdropFilter: 'blur(10px)' }}>
                    <Typography variant="h6" gutterBottom>Assign Student to Lab Group</Typography>
                    <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
                        <FormControl sx={{ minWidth: 200 }}>
                            <InputLabel>Select Lab Group</InputLabel>
                            <Select
                                value={assignGroupId}
                                label="Select Lab Group"
                                onChange={(e) => setAssignGroupId(e.target.value)}
                            >
                                {labGroups.map((group) => (
                                    <MenuItem key={group._id} value={group._id}>
                                        {group.groupName}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl sx={{ minWidth: 250 }}>
                            <InputLabel>Select Student</InputLabel>
                            <Select
                                value={assignStudentId}
                                label="Select Student"
                                onChange={(e) => setAssignStudentId(e.target.value)}
                            >
                                {students
                                    .filter(s => s.sclassName._id === selectedClass || s.sclassName === selectedClass)
                                    .map((student) => (
                                    <MenuItem key={student._id} value={student._id}>
                                        {student.name} (Roll: {student.rollNum})
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        
                        <Button variant="contained" color="secondary" onClick={handleAssignStudent}>
                            Assign
                        </Button>
                    </Box>
                </Paper>
            )}

            {selectedClass && (
                <>
                    <Typography variant="h5" gutterBottom>Existing Lab Groups in Class</Typography>
                    {loading ? <CircularProgress /> : (
                        <TableContainer component={Paper} sx={{ backgroundColor: 'rgba(30, 30, 46, 0.7)', backdropFilter: 'blur(10px)' }}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Group Name</TableCell>
                                        <TableCell>Students</TableCell>
                                        <TableCell>Count</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {labGroups.map((group) => (
                                        <TableRow key={group._id}>
                                            <TableCell>{group.groupName}</TableCell>
                                            <TableCell>
                                                {group.students?.map(s => `${s.name} (${s.rollNum})`).join(', ') || 'No students'}
                                            </TableCell>
                                            <TableCell>{group.students?.length || 0}</TableCell>
                                        </TableRow>
                                    ))}
                                    {labGroups.length === 0 && (
                                        <TableRow>
                                            <TableCell colSpan={3}>No lab groups found.</TableCell>
                                        </TableRow>
                                    )}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    )}
                </>
            )}

            <Popup message={message} setShowPopup={setShowPopup} showPopup={showPopup} />
        </Box>
    );
};

export default LabGroupManagement;
