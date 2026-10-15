import React from 'react';
import { Link } from 'react-router-dom';
import { Container, Grid, Box, Button } from '@mui/material';
import styled from 'styled-components';
import Students from "../assets/students.svg";
import { LightPurpleButton } from '../components/buttonStyles';

const Homepage = () => {
    return (
        <StyledContainer>
            <Grid container spacing={0}>
                <Grid item xs={12} md={6}>
                    <img src={Students} alt="students" style={{ width: '100%' }} />
                </Grid>
                <Grid item xs={12} md={6}>
                    <StyledPaper elevation={3}>
                        <StyledTitle>
                            Welcome to
                            <br />
                            College Management
                            <br />
                            System
                        </StyledTitle>
                        <StyledText>
                            Streamline college management, class organization, and add students and faculty.
                            Seamlessly track attendance, assess performance, and provide feedback.
                            Access records, view marks, and communicate effortlessly.
                        </StyledText>
                        <StyledBox>
                            <StyledLink to="/choose">
                                <LightPurpleButton variant="contained" fullWidth>
                                    Login
                                </LightPurpleButton>
                            </StyledLink>
                            <StyledLink to="/chooseasguest">
                                <Button variant="outlined" fullWidth
                                    sx={{ mt: 2, mb: 3, color: "#7f56da", borderColor: "#7f56da" }}
                                >
                                    Login as Guest
                                </Button>
                            </StyledLink>
                            <StyledText style={{ textAlign: 'center' }}>
                                Don't have an account?{' '}
                                <Link to="/Adminregister" style={{color:"#b085f5", fontWeight: 'bold'}}>
                                    Sign up
                                </Link>
                            </StyledText>
                        </StyledBox>
                    </StyledPaper>
                </Grid>
            </Grid>
        </StyledContainer>
    );
};

export default Homepage;

const StyledContainer = styled(Container)`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 0 !important;
`;

const StyledPaper = styled.div`
  padding: 40px;
  margin: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
`;

const StyledBox = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content:center;
  gap: 16px;
  padding: 24px;
`;

const StyledTitle = styled.h1`
  font-size: 3.5rem;
  color: #fff;
  font-weight: 800;
  padding-top: 0;
  letter-spacing: -1px;
  line-height: 1.1;
  background: linear-gradient(90deg, #b085f5, #7e57c2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
`;

const StyledText = styled.p`
  color: rgba(255, 255, 255, 0.7);
  margin-top: 20px;
  margin-bottom: 30px; 
  letter-spacing: 0.5px;
  line-height: 1.6;
  font-size: 1.1rem;
`;

const StyledLink = styled(Link)`
  text-decoration: none;
`;
