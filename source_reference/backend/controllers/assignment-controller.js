const Assignment = require('../models/assignmentSchema');
const AssignmentSubmission = require('../models/assignmentSubmissionSchema');
const { GoogleGenAI } = require('@google/genai');

const createAssignment = async (req, res) => {
    try {
        const assignment = new Assignment(req.body);
        const result = await assignment.save();
        res.send(result);
    } catch (err) {
        res.status(500).json(err);
    }
};

const getAssignmentsByClass = async (req, res) => {
    try {
        const assignments = await Assignment.find({ sclassName: req.params.id })
            .populate("subject", "subName")
            .populate("teacher", "name");
        res.send(assignments);
    } catch (err) {
        res.status(500).json(err);
    }
};

const getSubmissionsByAssignment = async (req, res) => {
    try {
        const submissions = await AssignmentSubmission.find({ assignment: req.params.id })
            .populate("student", "name rollNum")
            .populate("plagiarizedFrom", "name rollNum");
        res.send(submissions);
    } catch (err) {
        res.status(500).json(err);
    }
};

// Simple Jaccard similarity for text
function calculateSimilarity(text1, text2) {
    const words1 = new Set(text1.toLowerCase().split(/\W+/));
    const words2 = new Set(text2.toLowerCase().split(/\W+/));
    const intersection = new Set([...words1].filter(x => words2.has(x)));
    const union = new Set([...words1, ...words2]);
    return intersection.size / union.size;
}

const submitAssignment = async (req, res) => {
    try {
        const { student, assignment, content } = req.body;

        // 1. Check for plagiarism
        const previousSubmissions = await AssignmentSubmission.find({ assignment });
        let plagiarizedFrom = [];
        for (let sub of previousSubmissions) {
            if (sub.student.toString() !== student.toString()) {
                const similarity = calculateSimilarity(content, sub.content);
                // If similarity > 70%, flag as plagiarized
                if (similarity > 0.7) {
                    plagiarizedFrom.push(sub.student);
                    
                    // Also flag the other student
                    if (!sub.plagiarizedFrom.includes(student)) {
                        sub.plagiarizedFrom.push(student);
                        await sub.save();
                    }
                }
            }
        }

        // 2. Generate Summary with AI
        let summary = "Summary unavailable.";
        try {
            const ai = new GoogleGenAI({}); // Requires GEMINI_API_KEY env var
            const prompt = `Summarize the following student assignment in 1-2 sentences: \n\n"${content}"`;
            const response = await ai.models.generateContent({
                model: 'gemini-2.5-flash',
                contents: prompt,
            });
            summary = response.text;
        } catch (aiError) {
            console.error("AI summarization failed, probably missing API key", aiError.message);
        }

        // 3. Save Submission
        const newSubmission = new AssignmentSubmission({
            student,
            assignment,
            content,
            summary,
            plagiarizedFrom
        });
        const result = await newSubmission.save();
        res.send(result);
    } catch (err) {
        console.error(err);
        res.status(500).json(err);
    }
};

module.exports = {
    createAssignment,
    getAssignmentsByClass,
    submitAssignment,
    getSubmissionsByAssignment
};
