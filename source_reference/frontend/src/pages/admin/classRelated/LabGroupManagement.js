import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
    Box, Paper, Typography, Button, TextField, CircularProgress, 
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Select, MenuItem, FormControl, InputLabel
} from '@mui/material';
import axios from 'axios';
import Popup from '../../../components/Popup';
import { getAllSclasses } from '../../../redux/sclassRelated/sclassHandle';

const LabGroupManagement = () => {
    const dispatch = useDispatch();
    const { currentUser } = useSelector((state) => state.user);
    const { sclassesList } = useSelector((state) => state.sclass);

    const [labGroups, setLabGroups] = useState([]);
    const [loading, setLoading] = useState(false);
    
    const [groupName, setGroupName] = useState('');
    const [selectedClass, setSelectedClass] = useState('');

    const [showPopup, setShowPopup] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        dispatch(getAllSclasses(currentUser._id, "Sclass"));
    }, [dispatch, currentUser._id]);

    useEffect(() => {
        if (selectedClass) {
            fetchLabGroups();
        }
    }, [selectedClass]);

    const fetchLabGroups = async () => {
        setLoading(true);
        try {
            const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/LabGroupsClass/${selectedClass}`);
            setLabGroups(res.data);
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
                school: currentUser._id,
                students: []
            });
            setMessage("Lab Group created successfully!");
            setShowPopup(true);
            setGroupName('');
            fetchLabGroups();
        } catch (error) {
            setMessage("Failed to create lab group");
            setShowPopup(true);
            console.error(error);
        }
    };

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
                Lab Group Management
            </Typography>

            <Paper sx={{ p: 3, mb: 4 }}>
                <Typography variant="h6" gutterBottom>Create New Lab Group</Typography>
                
                <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
                    <FormControl fullWidth>
                        <InputLabel>Select Class</InputLabel>
                        <Select
                            value={selectedClass}
                            label="Select Class"
                            onChange={(e) => setSelectedClass(e.target.value)}
                        >
                            {sclassesList && sclassesList.map((sclass) => (
                                <MenuItem key={sclass._id} value={sclass._id}>
                                    {sclass.sclassName}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <TextField 
                        label="Lab Group Name (e.g. Group A)" 
                        value={groupName} 
                        onChange={(e) => setGroupName(e.target.value)} 
                        fullWidth 
                    />
                </Box>

                <Button variant="contained" color="primary" onClick={handleCreateLabGroup}>
                    Create Lab Group
                </Button>
            </Paper>

            {selectedClass && (
                <>
                    <Typography variant="h5" gutterBottom>Existing Lab Groups in Class</Typography>
                    {loading ? <CircularProgress /> : (
                        <TableContainer component={Paper}>
                            <Table>
                                <TableHead>
                                    <TableRow>
                                        <TableCell>Group Name</TableCell>
                                        <TableCell>Students Count</TableCell>
                                        <TableCell>Action</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {labGroups.map((group) => (
                                        <TableRow key={group._id}>
                                            <TableCell>{group.groupName}</TableCell>
                                            <TableCell>{group.students?.length || 0}</TableCell>
                                            <TableCell>
                                                {/* Future: Add button to assign students explicitly if needed */}
                                                <Button size="small" variant="outlined">Manage Students</Button>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                    {labGroups.length === 0 && (
                                        <TableRow>
                                            <TableCell colSpan={3} align="center">No Lab Groups found.</TableCell>
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
