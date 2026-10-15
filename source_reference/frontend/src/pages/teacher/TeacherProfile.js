import React from 'react'
import { Box, Typography, Paper, Avatar } from '@mui/material';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import { useSelector } from 'react-redux';

const TeacherProfile = () => {
  const { currentUser, response, error } = useSelector((state) => state.user);

  if (response) { console.log(response) }
  else if (error) { console.log(error) }

  const teachSclass = currentUser.teachSclass
  const teachSubject = currentUser.teachSubject
  const teachSchool = currentUser.school

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
                Teacher
            </Typography>

            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3, textAlign: 'left', pl: 2, borderLeft: '3px solid #8E54E9' }}>
                <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
                    <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '120px' }}>Email:</span> 
                    {currentUser.email}
                </Typography>
                <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
                    <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '120px' }}>Class:</span> 
                    {teachSclass.sclassName}
                </Typography>
                <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
                    <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '120px' }}>Subject:</span> 
                    {teachSubject.subName}
                </Typography>
                <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
                    <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '120px' }}>College:</span> 
                    {teachSchool.schoolName}
                </Typography>
            </Box>
        </Paper>
    </Box>
  )
}

export default TeacherProfile
