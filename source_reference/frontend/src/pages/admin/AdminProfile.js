// import React, { useState } from 'react';
// import { KeyboardArrowDown, KeyboardArrowUp } from '@mui/icons-material'
// import { useDispatch, useSelector } from 'react-redux';
// import { deleteUser, updateUser } from '../../redux/userRelated/userHandle';
// import { useNavigate } from 'react-router-dom'
// import { authLogout } from '../../redux/userRelated/userSlice';
// import { Button, Collapse } from '@mui/material';

import { useSelector } from 'react-redux';
import { Box, Typography, Paper, Avatar } from '@mui/material';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';

const AdminProfile = () => {
    // const [showTab, setShowTab] = useState(false);
    // const buttonText = showTab ? 'Cancel' : 'Edit profile';

    // const navigate = useNavigate()
    // const dispatch = useDispatch();
        const { currentUser } = useSelector((state) => state.user);
    // const { currentUser, response, error } = useSelector((state) => state.user);
    // const address = "Admin"

    // if (response) { console.log(response) }
    // else if (error) { console.log(error) }

    // const [name, setName] = useState(currentUser.name);
    // const [email, setEmail] = useState(currentUser.email);
    // const [password, setPassword] = useState("");
    // const [schoolName, setSchoolName] = useState(currentUser.schoolName);

    // const fields = password === "" ? { name, email, schoolName } : { name, email, password, schoolName }

    // const submitHandler = (event) => {
    //     event.preventDefault()
    //     dispatch(updateUser(fields, currentUser._id, address))
    // }

    // const deleteHandler = () => {
    //     try {
    //         dispatch(deleteUser(currentUser._id, "Students"));
    //         dispatch(deleteUser(currentUser._id, address));
    //         dispatch(authLogout());
    //         navigate('/');
    //     } catch (error) {
    //         console.error(error);
    //     }
    // }

    return (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 8 }}>
            <Paper elevation={0} sx={{
                p: 5,
                borderRadius: '24px',
                background: 'rgba(255, 255, 255, 0.03)',
                backdropFilter: 'blur(20px)',
                border: '1px solid rgba(255, 255, 255, 0.05)',
                boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
                width: '100%',
                maxWidth: '500px',
                textAlign: 'center'
            }}>
                <Avatar sx={{ 
                    width: 100, 
                    height: 100, 
                    margin: '0 auto', 
                    mb: 3, 
                    bgcolor: 'rgba(126, 87, 194, 0.2)',
                    color: '#b085f5'
                }}>
                    <AccountCircleIcon sx={{ fontSize: 60 }} />
                </Avatar>

                <Typography variant="h4" sx={{ fontWeight: 700, mb: 1, color: '#fff', letterSpacing: '1px' }}>
                    {currentUser.name}
                </Typography>
                
                <Typography variant="subtitle1" sx={{ color: '#a0a0b0', mb: 4, textTransform: 'uppercase', letterSpacing: '2px', fontSize: '0.8rem' }}>
                    Administrator
                </Typography>

                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3, textAlign: 'left', pl: 2, borderLeft: '3px solid #8E54E9' }}>
                    <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
                        <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '120px' }}>Email:</span> 
                        {currentUser.email}
                    </Typography>
                    <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
                        <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '120px' }}>College:</span> 
                        {currentUser.schoolName}
                    </Typography>
                </Box>
            </Paper>
        </Box>
    )
}

export default AdminProfile

// const styles = {
//     attendanceButton: {
//         backgroundColor: "#270843",
//         "&:hover": {
//             backgroundColor: "#3f1068",
//         }
//     }
// }