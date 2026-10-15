import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
    Box, Paper, Typography, Button, TextField, CircularProgress, 
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, IconButton
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import axios from 'axios';
import Popup from '../../components/Popup';

const QuizManagement = () => {
    const { currentUser } = useSelector((state) => state.user);

    const classID = currentUser.teachSclass?._id;
    const subjectID = currentUser.teachSubject?._id;
    const teacherID = currentUser._id;

    const [quizzes, setQuizzes] = useState([]);
    const [loading, setLoading] = useState(false);
    
    // New Quiz state
    const [title, setTitle] = useState('');
    const [questions, setQuestions] = useState([]);
    
    // Manual Question state
    const [manualQuestion, setManualQuestion] = useState({ questionText: '', options: [{ optionText: '', isCorrect: true }, { optionText: '', isCorrect: false }, { optionText: '', isCorrect: false }, { optionText: '', isCorrect: false }] });
    
    // AI Generation state
    const [aiTopic, setAiTopic] = useState('');
    const [referenceNotes, setReferenceNotes] = useState('');
    const [generating, setGenerating] = useState(false);

    const [showPopup, setShowPopup] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        fetchQuizzes();
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

    const generateQuestions = async () => {
        if (!aiTopic) {
            setMessage("Please enter a topic for the AI");
            setShowPopup(true);
            return;
        }

        setGenerating(true);
        try {
            const res = await axios.post(`${process.env.REACT_APP_BASE_URL}/QuizGenerateAI`, {
                topic: aiTopic,
                referenceNotes: referenceNotes,
                numQuestions: 5
            });
            setQuestions(res.data);
            setMessage("Questions generated successfully!");
            setShowPopup(true);
        } catch (error) {
            setMessage("Failed to generate questions. Ensure Gemini API key is set.");
            setShowPopup(true);
            console.error(error);
        } finally {
            setGenerating(false);
        }
    };

    const handleManualOptionChange = (idx, value) => {
        const newOptions = [...manualQuestion.options];
        newOptions[idx].optionText = value;
        setManualQuestion({ ...manualQuestion, options: newOptions });
    };

    const handleManualCorrectChange = (idx) => {
        const newOptions = manualQuestion.options.map((opt, i) => ({
            ...opt,
            isCorrect: i === idx
        }));
        setManualQuestion({ ...manualQuestion, options: newOptions });
    };

    const addManualQuestion = () => {
        if (!manualQuestion.questionText) {
            setMessage("Question text is required");
            setShowPopup(true);
            return;
        }
        setQuestions([...questions, manualQuestion]);
        setManualQuestion({ questionText: '', options: [{ optionText: '', isCorrect: true }, { optionText: '', isCorrect: false }, { optionText: '', isCorrect: false }, { optionText: '', isCorrect: false }] });
    };

    const handleCreateQuiz = async () => {
        if (!title || questions.length === 0) {
            setMessage("Title and questions are required");
            setShowPopup(true);
            return;
        }

        try {
            await axios.post(`${process.env.REACT_APP_BASE_URL}/QuizCreate`, {
                title,
                subject: subjectID,
                sclassName: classID,
                school: currentUser.school,
                createdBy: teacherID,
                questions
            });
            setMessage("Quiz created successfully!");
            setShowPopup(true);
            setTitle('');
            setQuestions([]);
            fetchQuizzes();
        } catch (error) {
            setMessage("Failed to create quiz");
            setShowPopup(true);
            console.error(error);
        }
    };

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
                Quiz Management
            </Typography>

            <Paper sx={{ p: 3, mb: 4 }}>
                <Typography variant="h6" gutterBottom>Create New Quiz</Typography>
                
                <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
                    <TextField 
                        label="Quiz Title" 
                        value={title} 
                        onChange={(e) => setTitle(e.target.value)} 
                        fullWidth 
                    />
                </Box>

                <Paper variant="outlined" sx={{ p: 2, mb: 3, backgroundColor: 'rgba(255, 255, 255, 0.05)' }}>
                    <Typography variant="subtitle1" gutterBottom>
                        <AutoAwesomeIcon color="primary" sx={{ mr: 1, verticalAlign: 'middle' }}/> 
                        Generate Questions with AI (Gemini)
                    </Typography>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <TextField 
                            label="Topic or Concept" 
                            value={aiTopic} 
                            onChange={(e) => setAiTopic(e.target.value)} 
                            fullWidth 
                            size="small"
                        />
                        <TextField 
                            label="Reference Material / Teacher Notes (Optional)" 
                            value={referenceNotes} 
                            onChange={(e) => setReferenceNotes(e.target.value)} 
                            fullWidth 
                            multiline
                            rows={4}
                            placeholder="Paste your lecture notes, textbook excerpts, or specific facts you want the questions to be based on..."
                        />
                        <Box>
                            <Button 
                                variant="contained" 
                                color="secondary" 
                                onClick={generateQuestions}
                                disabled={generating}
                            >
                                {generating ? <CircularProgress size={24} /> : "Generate"}
                            </Button>
                        </Box>
                    </Box>
                </Paper>

                <Paper variant="outlined" sx={{ p: 2, mb: 3, backgroundColor: 'rgba(255, 255, 255, 0.05)' }}>
                    <Typography variant="subtitle1" gutterBottom>
                        Manually Add Question
                    </Typography>
                    <TextField
                        label="Question Text"
                        value={manualQuestion.questionText}
                        onChange={(e) => setManualQuestion({ ...manualQuestion, questionText: e.target.value })}
                        fullWidth
                        size="small"
                        sx={{ mb: 2 }}
                    />
                    {manualQuestion.options.map((opt, idx) => (
                        <Box key={idx} sx={{ display: 'flex', gap: 2, mb: 1, alignItems: 'center' }}>
                            <input
                                type="radio"
                                name="correctOption"
                                checked={opt.isCorrect}
                                onChange={() => handleManualCorrectChange(idx)}
                            />
                            <TextField
                                label={`Option ${idx + 1}`}
                                value={opt.optionText}
                                onChange={(e) => handleManualOptionChange(idx, e.target.value)}
                                fullWidth
                                size="small"
                            />
                        </Box>
                    ))}
                    <Button variant="outlined" color="primary" onClick={addManualQuestion} sx={{ mt: 1 }}>
                        Add Question
                    </Button>
                </Paper>

                {questions.length > 0 && (
                    <Box sx={{ mb: 3 }}>
                        <Typography variant="h6">Questions Preview:</Typography>
                        {questions.map((q, idx) => (
                            <Box key={idx} sx={{ mt: 2, p: 2, border: '1px solid rgba(255, 255, 255, 0.2)', borderRadius: 2 }}>
                                <Typography fontWeight="bold">{idx + 1}. {q.questionText}</Typography>
                                <ul>
                                    {q.options.map((opt, oIdx) => (
                                        <li key={oIdx} style={{ color: opt.isCorrect ? '#4caf50' : 'rgba(255, 255, 255, 0.7)', fontWeight: opt.isCorrect ? 'bold' : 'normal' }}>
                                            {opt.optionText}
                                        </li>
                                    ))}
                                </ul>
                            </Box>
                        ))}
                    </Box>
                )}

                <Button variant="contained" color="primary" onClick={handleCreateQuiz}>
                    Save Quiz
                </Button>
            </Paper>

            <Typography variant="h5" gutterBottom>Existing Quizzes</Typography>
            {loading ? <CircularProgress /> : (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Title</TableCell>
                                <TableCell>Questions count</TableCell>
                                <TableCell>Created At</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {quizzes.map((quiz) => (
                                <TableRow key={quiz._id}>
                                    <TableCell>{quiz.title}</TableCell>
                                    <TableCell>{quiz.questions?.length}</TableCell>
                                    <TableCell>{new Date(quiz.createdAt).toLocaleDateString()}</TableCell>
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

export default QuizManagement;
