import React from 'react'

import { Box, Typography, Paper } from '@mui/material';
import ConstructionIcon from '@mui/icons-material/Construction';

const TeacherComplain = () => {
  return (
    <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
        <Paper elevation={0} sx={{ 
            p: 6, 
            borderRadius: '24px', 
            background: 'rgba(255, 255, 255, 0.03)', 
            backdropFilter: 'blur(20px)',
            border: '1px solid rgba(255, 255, 255, 0.05)',
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
            textAlign: 'center',
            maxWidth: '400px'
        }}>
            <ConstructionIcon sx={{ fontSize: 80, color: 'rgba(255,255,255,0.2)', mb: 2 }} />
            <Typography variant="h5" sx={{ color: '#fff', mb: 1, fontWeight: 'bold' }}>
                Under Construction
            </Typography>
            <Typography variant="body1" sx={{ color: 'rgba(255,255,255,0.5)', mb: 2 }}>
                The complaints module is currently being developed. Check back later!
            </Typography>
        </Paper>
    </Box>
  )
}

export default TeacherComplain