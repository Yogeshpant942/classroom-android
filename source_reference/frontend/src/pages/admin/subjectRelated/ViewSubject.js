import React, { useEffect, useState } from 'react'
import { getClassStudents, getSubjectDetails } from '../../../redux/sclassRelated/sclassHandle';
import { useNavigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { Box, Tab, Container, Typography, BottomNavigation, BottomNavigationAction, Paper } from '@mui/material';
import { BlueButton, GreenButton, PurpleButton } from '../../../components/buttonStyles';
import TableTemplate from '../../../components/TableTemplate';
import TabContext from '@mui/lab/TabContext';
import TabList from '@mui/lab/TabList';
import TabPanel from '@mui/lab/TabPanel';

import InsertChartIcon from '@mui/icons-material/InsertChart';
import InsertChartOutlinedIcon from '@mui/icons-material/InsertChartOutlined';
import TableChartIcon from '@mui/icons-material/TableChart';
import TableChartOutlinedIcon from '@mui/icons-material/TableChartOutlined';

const ViewSubject = () => {
  const navigate = useNavigate()
  const params = useParams()
  const dispatch = useDispatch();
  const { subloading, subjectDetails, sclassStudents, getresponse, error } = useSelector((state) => state.sclass);

  const { classID, subjectID } = params

  useEffect(() => {
    dispatch(getSubjectDetails(subjectID, "Subject"));
    dispatch(getClassStudents(classID));
  }, [dispatch, subjectID, classID]);

  if (error) {
    console.log(error)
  }

  const [value, setValue] = useState('1');

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const [selectedSection, setSelectedSection] = useState('attendance');
  const handleSectionChange = (event, newSection) => {
    setSelectedSection(newSection);
  };

  const studentColumns = [
    { id: 'rollNum', label: 'Roll No.', minWidth: 100 },
    { id: 'name', label: 'Name', minWidth: 170 },
  ]

  const studentRows = sclassStudents.map((student) => {
    return {
      rollNum: student.rollNum,
      name: student.name,
      id: student._id,
    };
  })

  const StudentsAttendanceButtonHaver = ({ row }) => {
    return (
      <>
        <BlueButton
          variant="contained"
          onClick={() => navigate("/Admin/students/student/" + row.id)}
        >
          View
        </BlueButton>
        <PurpleButton
          variant="contained"
          onClick={() =>
            navigate(`/Admin/subject/student/attendance/${row.id}/${subjectID}`)
          }
        >
          Take Attendance
        </PurpleButton>
      </>
    );
  };

  const StudentsMarksButtonHaver = ({ row }) => {
    return (
      <>
        <BlueButton
          variant="contained"
          onClick={() => navigate("/Admin/students/student/" + row.id)}
        >
          View
        </BlueButton>
        <PurpleButton variant="contained"
          onClick={() => navigate(`/Admin/subject/student/marks/${row.id}/${subjectID}`)}>
          Provide Marks
        </PurpleButton>
      </>
    );
  };

  const SubjectStudentsSection = () => {
    return (
      <>
        {getresponse ? (
          <>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', marginTop: '16px' }}>
              <GreenButton
                variant="contained"
                onClick={() => navigate("/Admin/class/addstudents/" + classID)}
              >
                Add Students
              </GreenButton>
            </Box>
          </>
        ) : (
          <>
            <Typography variant="h5" gutterBottom>
              Students List:
            </Typography>

            {selectedSection === 'attendance' &&
              <TableTemplate buttonHaver={StudentsAttendanceButtonHaver} columns={studentColumns} rows={studentRows} />
            }
            {selectedSection === 'marks' &&
              <TableTemplate buttonHaver={StudentsMarksButtonHaver} columns={studentColumns} rows={studentRows} />
            }

            <Paper sx={{ position: 'fixed', bottom: 0, left: 0, right: 0 }} elevation={3}>
              <BottomNavigation value={selectedSection} onChange={handleSectionChange} showLabels>
                <BottomNavigationAction
                  label="Attendance"
                  value="attendance"
                  icon={selectedSection === 'attendance' ? <TableChartIcon /> : <TableChartOutlinedIcon />}
                />
                <BottomNavigationAction
                  label="Marks"
                  value="marks"
                  icon={selectedSection === 'marks' ? <InsertChartIcon /> : <InsertChartOutlinedIcon />}
                />
              </BottomNavigation>
            </Paper>

          </>
        )}
      </>
    )
  }

  const SubjectDetailsSection = () => {
    const numberOfStudents = sclassStudents.length;

    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 4 }}>
        <Paper elevation={0} sx={{ 
            p: 5, 
            borderRadius: '24px', 
            background: 'rgba(255, 255, 255, 0.03)', 
            backdropFilter: 'blur(20px)',
            border: '1px solid rgba(255, 255, 255, 0.05)',
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
            width: '100%',
            maxWidth: '600px',
            textAlign: 'center'
        }}>
          <Typography variant="h3" sx={{ fontWeight: 700, mb: 4, color: '#fff', letterSpacing: '1px' }}>
            Subject Details
          </Typography>

          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3, mb: 4, textAlign: 'left', pl: 2, borderLeft: '3px solid #8E54E9' }}>
            <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
              <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '180px' }}>Subject Name:</span> 
              {subjectDetails && subjectDetails.subName}
            </Typography>
            <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
              <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '180px' }}>Subject Code:</span> 
              {subjectDetails && subjectDetails.subCode}
            </Typography>
            <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
              <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '180px' }}>Sessions:</span> 
              {subjectDetails && subjectDetails.sessions}
            </Typography>
            <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
              <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '180px' }}>Enrolled Students:</span> 
              {numberOfStudents}
            </Typography>
            <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
              <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '180px' }}>Class:</span> 
              {subjectDetails && subjectDetails.sclassName && subjectDetails.sclassName.sclassName}
            </Typography>
            
            {subjectDetails && subjectDetails.teacher ? (
              <Typography variant="h6" sx={{ color: '#e0e0e0', fontWeight: 400 }}>
                <span style={{ fontWeight: 700, color: '#fff', display: 'inline-block', width: '180px' }}>Assigned Teacher:</span> 
                {subjectDetails.teacher.name}
              </Typography>
            ) : null}
          </Box>

          {!subjectDetails?.teacher && (
            <GreenButton 
                variant="contained"
                size="large"
                fullWidth
                sx={{ py: 1.5, mt: 2, fontSize: '1.1rem', borderRadius: '12px' }}
                onClick={() => navigate("/Admin/teachers/addteacher/" + subjectDetails._id)}
            >
              Assign Subject Teacher
            </GreenButton>
          )}
        </Paper>
      </Box>
    );
  }

  return (
    <>
      {subloading ?
        < div > Loading...</div >
        :
        <>
          <Box sx={{ width: '100%', typography: 'body1', mt: 3 }} >
            <TabContext value={value}>
              <Box sx={{ display: 'flex', justifyContent: 'center', mb: 3 }}>
                <TabList 
                    onChange={handleChange} 
                    sx={{ 
                        bgcolor: 'rgba(255,255,255,0.05)', 
                        borderRadius: '16px', 
                        p: 0.5,
                        '& .MuiTab-root': { color: 'rgba(255,255,255,0.5)', borderRadius: '12px', minWidth: '150px' },
                        '& .Mui-selected': { color: '#fff !important', bgcolor: 'rgba(255,255,255,0.1)' },
                        '& .MuiTabs-indicator': { display: 'none' }
                    }}
                >
                  <Tab label="Subject Details" value="1" />
                  <Tab label="Manage Students" value="2" />
                </TabList>
              </Box>
              <Container maxWidth="xl" sx={{ mb: "4rem" }}>
                <TabPanel value="1" sx={{ p: 0 }}>
                  <SubjectDetailsSection />
                </TabPanel>
                <TabPanel value="2" sx={{ p: 0 }}>
                  <SubjectStudentsSection />
                </TabPanel>
              </Container>
            </TabContext>
          </Box>
        </>
      }
    </>
  )
}

export default ViewSubject