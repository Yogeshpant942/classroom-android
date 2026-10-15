import { Container, Grid, Paper } from '@mui/material'
import SeeNotice from '../../components/SeeNotice';
import CountUp from 'react-countup';
import styled from 'styled-components';
import Students from "../../assets/img1.png";
import Lessons from "../../assets/subjects.svg";
import Tests from "../../assets/assignment.svg";
import Time from "../../assets/time.svg";
import { getClassStudents, getSubjectDetails } from '../../redux/sclassRelated/sclassHandle';
import { useDispatch, useSelector } from 'react-redux';
import { useEffect } from 'react';

const TeacherHomePage = () => {
    const dispatch = useDispatch();

    const { currentUser } = useSelector((state) => state.user);
    const { subjectDetails, sclassStudents } = useSelector((state) => state.sclass);

    const classID = currentUser.teachSclass?._id
    const subjectID = currentUser.teachSubject?._id

    useEffect(() => {
        dispatch(getSubjectDetails(subjectID, "Subject"));
        dispatch(getClassStudents(classID));
    }, [dispatch, subjectID, classID]);

    const numberOfStudents = Array.isArray(sclassStudents) ? sclassStudents.length : 0;
    const numberOfSessions = subjectDetails && subjectDetails.sessions ? subjectDetails.sessions : 0;

    return (
        <>
            <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
                <Grid container spacing={3}>
                    <Grid item xs={12} md={6} lg={6}>
                        <StyledPaper>
                            <img src={Students} alt="Students" />
                            <Title>
                                Class Students
                            </Title>
                            <Data start={0} end={numberOfStudents} duration={2.5} />
                        </StyledPaper>
                    </Grid>
                    <Grid item xs={12} md={6} lg={6}>
                        <StyledPaper>
                            <img src={Lessons} alt="Lessons" />
                            <Title>
                                Total Lessons
                            </Title>
                            <Data start={0} end={numberOfSessions} duration={5} />
                        </StyledPaper>
                    </Grid>
                    <Grid item xs={12} md={12} lg={12}>
                        <Paper sx={{ 
                            p: 3, 
                            display: 'flex', 
                            flexDirection: 'column',
                            background: 'rgba(255, 255, 255, 0.03)',
                            backdropFilter: 'blur(20px)',
                            borderRadius: '24px',
                            border: '1px solid rgba(255, 255, 255, 0.05)',
                            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)'
                        }}>
                            <SeeNotice />
                        </Paper>
                    </Grid>
                </Grid>
            </Container>
        </>
    )
}

const StyledPaper = styled(Paper)`
  padding: 24px;
  display: flex;
  flex-direction: column;
  height: 220px;
  justify-content: space-between;
  align-items: center;
  text-align: center;
  background: rgba(255, 255, 255, 0.03) !important;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 24px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease-in-out;
  cursor: pointer;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.5) !important;
    background: rgba(255, 255, 255, 0.05) !important;
  }

  img {
    width: 64px;
    height: 64px;
    margin-bottom: 12px;
    filter: drop-shadow(0 4px 8px rgba(0,0,0,0.3));
  }
`;

const Title = styled.p`
  font-size: 1.1rem;
  font-weight: 500;
  color: #a0a0b0;
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 1px;
`;

const Data = styled(CountUp)`
  font-size: calc(2rem + 0.5vw);
  font-weight: 800;
  color: #fff;
  background: linear-gradient(135deg, #00f2fe 0%, #4facfe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
`;

export default TeacherHomePage