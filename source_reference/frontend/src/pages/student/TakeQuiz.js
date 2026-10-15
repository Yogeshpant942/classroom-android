import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import {
    Box, Paper, Typography, Button, CircularProgress,
    List, ListItem, ListItemText, Radio, Grid
} from '@mui/material';
import axios from 'axios';
import Popup from '../../components/Popup';

const TakeQuiz = () => {
    const { currentUser } = useSelector((state) => state.user);
    const studentID = currentUser._id;
    const classID = currentUser.sclassName?._id;

    const [quizzes, setQuizzes] = useState([]);
    const [loading, setLoading] = useState(false);
    const [selectedQuiz, setSelectedQuiz] = useState(null);
    const [answers, setAnswers] = useState({});
    const [submitting, setSubmitting] = useState(false);
    const [quizResult, setQuizResult] = useState(null);
    
    const [showPopup, setShowPopup] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        if (classID) {
            fetchQuizzes();
        }
    }, [classID]);

    const fetchQuizzes = async () => {
        setLoading(true);
        try {
            const res = await axios.get(`${process.env.REACT_APP_BASE_URL}/QuizzesClass/${classID}`);
            setQuizzes(res.data);
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleOptionSelect = (questionId, optionId) => {
        setAnswers(prev => ({
            ...prev,
            [questionId]: optionId
        }));
    };

    const handleSubmitQuiz = async () => {
        const formattedAnswers = Object.keys(answers).map(qId => ({
            questionId: qId,
            selectedOptionId: answers[qId]
        }));

        setSubmitting(true);
        try {
            const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/QuizSubmit`, {
                student: studentID,
                quiz: selectedQuiz._id,
                answers: formattedAnswers
            });
            setMessage(`Quiz submitted! Your score is ${res.data.score}`);
            setShowPopup(true);
            setQuizResult(res.data);
            // DO NOT clear selectedQuiz, so we can show the review mode
        } catch (error) {
            setMessage("Failed to submit quiz");
            setShowPopup(true);
        } finally {
            setSubmitting(false);
        }
    };

    if (selectedQuiz) {
        return (
            <Box sx={{ p: 3 }}>
                <Typography variant="h4" gutterBottom>{selectedQuiz.title}</Typography>
                <Paper sx={{ p: 3, mb: 4, backgroundColor: 'rgba(30, 30, 46, 0.7)', backdropFilter: 'blur(10px)', color: '#fff' }}>
                    <Typography variant="h6" color="primary" gutterBottom>
                        OMR Answer Sheet
                    </Typography>
                    
                    {selectedQuiz.questions.map((q, qIndex) => (
                        <Box key={q._id} sx={{ mb: 4, borderBottom: '1px solid rgba(255, 255, 255, 0.2)', pb: 2 }}>
                            <Typography variant="subtitle1" fontWeight="bold">
                                {qIndex + 1}. {q.questionText}
                            </Typography>
                            
                            <Grid container spacing={2} sx={{ mt: 1 }}>
                                {q.options.map((opt, oIndex) => {
                                    let bgColor = 'transparent';
                                    let borderColor = 'rgba(255, 255, 255, 0.5)';
                                    let textColor = 'rgba(255, 255, 255, 0.8)';
                                    
                                    if (quizResult) {
                                        // Review mode logic
                                        const isSelected = answers[q._id] === opt._id;
                                        const isActuallyCorrect = opt.isCorrect;
                                        
                                        if (isActuallyCorrect) {
                                            bgColor = '#4caf50'; // Green for correct
                                            borderColor = '#4caf50';
                                            textColor = '#fff';
                                        } else if (isSelected && !isActuallyCorrect) {
                                            bgColor = '#f44336'; // Red for wrong selection
                                            borderColor = '#f44336';
                                            textColor = '#fff';
                                        }
                                    } else {
                                        // Take quiz logic
                                        if (answers[q._id] === opt._id) {
                                            bgColor = '#7e57c2';
                                            borderColor = '#7e57c2';
                                            textColor = '#fff';
                                        }
                                    }

                                    return (
                                        <Grid item xs={12} sm={6} key={opt._id} sx={{ display: 'flex', alignItems: 'center' }}>
                                            <Box sx={{ 
                                                display: 'flex', 
                                                alignItems: 'center', 
                                                justifyContent: 'center',
                                                width: 30, height: 30, 
                                                borderRadius: '50%', 
                                                border: `2px solid ${borderColor}`,
                                                backgroundColor: bgColor,
                                                color: textColor,
                                                mr: 2,
                                                cursor: quizResult ? 'default' : 'pointer',
                                                transition: '0.3s'
                                            }} onClick={() => !quizResult && handleOptionSelect(q._id, opt._id)}>
                                                {String.fromCharCode(65 + oIndex)}
                                            </Box>
                                            <Typography>{opt.optionText}</Typography>
                                        </Grid>
                                    );
                                })}
                            </Grid>
                        </Box>
                    ))}

                    <Box sx={{ display: 'flex', gap: 2, mt: 3, alignItems: 'center' }}>
                        {!quizResult ? (
                            <>
                                <Button 
                                    variant="contained" 
                                    color="primary" 
                                    onClick={handleSubmitQuiz}
                                    disabled={submitting}
                                >
                                    {submitting ? <CircularProgress size={24} /> : "Submit Quiz"}
                                </Button>
                                <Button variant="outlined" color="secondary" onClick={() => setSelectedQuiz(null)}>
                                    Cancel
                                </Button>
                            </>
                        ) : (
                            <>
                                <Typography variant="h6" sx={{ mr: 4, color: '#4caf50' }}>
                                    Final Score: {quizResult.score} / {selectedQuiz.questions.length}
                                </Typography>
                                <Button variant="contained" color="secondary" onClick={() => {
                                    setSelectedQuiz(null);
                                    setQuizResult(null);
                                    setAnswers({});
                                }}>
                                    Return to Quizzes
                                </Button>
                            </>
                        )}
                    </Box>
                </Paper>
                <Popup message={message} setShowPopup={setShowPopup} showPopup={showPopup} />
            </Box>
        );
    }

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>Available Quizzes</Typography>
            {loading ? <CircularProgress /> : (
                <List>
                    {quizzes.map((quiz) => (
                        <Paper key={quiz._id} sx={{ mb: 2 }}>
                            <ListItem>
                                <ListItemText 
                                    primary={quiz.title} 
                                    secondary={`Questions: ${quiz.questions?.length}`} 
                                />
                                <Button variant="contained" onClick={() => setSelectedQuiz(quiz)}>
                                    Take Quiz
                                </Button>
                            </ListItem>
                        </Paper>
                    ))}
                    {quizzes.length === 0 && <Typography>No quizzes available.</Typography>}
                </List>
            )}
            <Popup message={message} setShowPopup={setShowPopup} showPopup={showPopup} />
        </Box>
    );
};

export default TakeQuiz;
