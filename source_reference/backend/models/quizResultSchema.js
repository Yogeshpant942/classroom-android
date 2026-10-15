const mongoose = require("mongoose");

const quizResultSchema = new mongoose.Schema({
    student: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'student',
        required: true,
    },
    quiz: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'quiz',
        required: true,
    },
    score: {
        type: Number,
        required: true,
    },
    answers: [
        {
            questionId: {
                type: mongoose.Schema.Types.ObjectId,
                required: true,
            },
            selectedOptionId: {
                type: mongoose.Schema.Types.ObjectId,
            },
            isCorrect: {
                type: Boolean,
                default: false
            }
        }
    ]
}, { timestamps: true });

module.exports = mongoose.model("quizResult", quizResultSchema);
