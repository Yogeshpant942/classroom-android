const Quiz = require('../models/quizSchema');
const QuizResult = require('../models/quizResultSchema');
const { GoogleGenAI } = require('@google/genai');

const createQuiz = async (req, res) => {
    try {
        const quiz = new Quiz(req.body);
        const result = await quiz.save();
        res.send(result);
    } catch (err) {
        res.status(500).json(err);
    }
};

const getQuizzesByClass = async (req, res) => {
    try {
        const quizzes = await Quiz.find({ sclassName: req.params.id }).populate("subject", "subName");
        res.send(quizzes);
    } catch (err) {
        res.status(500).json(err);
    }
};

const getQuizDetails = async (req, res) => {
    try {
        const quiz = await Quiz.findById(req.params.id).populate("subject", "subName");
        res.send(quiz);
    } catch (err) {
        res.status(500).json(err);
    }
};

const submitQuiz = async (req, res) => {
    try {
        const { student, quiz, answers } = req.body;

        // Auto-evaluate the score (OMR Corrector logic)
        const quizData = await Quiz.findById(quiz);
        if (!quizData) {
            return res.send({ message: "Quiz not found" });
        }

        let score = 0;
        const processedAnswers = answers.map((ans) => {
            const question = quizData.questions.id(ans.questionId);
            const selectedOption = question.options.id(ans.selectedOptionId);
            
            const isCorrect = selectedOption ? selectedOption.isCorrect : false;
            if (isCorrect) score++;

            return {
                questionId: ans.questionId,
                selectedOptionId: ans.selectedOptionId,
                isCorrect: isCorrect
            };
        });

        const quizResult = new QuizResult({
            student,
            quiz,
            score,
            answers: processedAnswers
        });

        const result = await quizResult.save();
        res.send(result);

    } catch (err) {
        res.status(500).json(err);
    }
};

const generateQuestionsWithAI = async (req, res) => {
    try {
        const { topic, numQuestions, referenceNotes } = req.body;
        
        // Initialize Gemini API
        const ai = new GoogleGenAI({}); // Needs GEMINI_API_KEY env var
        
        let prompt = `Generate ${numQuestions || 5} multiple-choice questions about "${topic}".`;
        
        if (referenceNotes && referenceNotes.trim() !== '') {
            prompt += ` Base the questions specifically on the following reference material/notes:\n"""\n${referenceNotes}\n"""\n`;
        }

        prompt += `\nReturn ONLY a JSON array with this format: 
        [{"questionText": "Question 1?", "options": [{"optionText": "Option 1", "isCorrect": true}, {"optionText": "Option 2", "isCorrect": false}, {"optionText": "Option 3", "isCorrect": false}, {"optionText": "Option 4", "isCorrect": false}]}]`;

        const response = await ai.models.generateContent({
            model: 'gemini-2.5-flash',
            contents: prompt,
            config: {
                responseMimeType: "application/json",
            }
        });

        const generatedData = JSON.parse(response.text);
        res.send(generatedData);

    } catch (err) {
        console.log(err);
        res.status(500).json({ message: "Failed to generate questions", error: err.message });
    }
};

module.exports = {
    createQuiz,
    getQuizzesByClass,
    getQuizDetails,
    submitQuiz,
    generateQuestionsWithAI
};
