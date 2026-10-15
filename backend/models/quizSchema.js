const mongoose = require("mongoose");

const quizSchema = new mongoose.Schema({
    title: {
        type: String,
        required: true,
    },
    subject: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'subject',
        required: true,
    },
    sclassName: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'sclass',
        required: true,
    },
    school: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'admin'
    },
    createdBy: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'teacher',
        required: true,
    },
    questions: [
        {
            questionText: {
                type: String,
                required: true,
            },
            options: [
                {
                    optionText: {
                        type: String,
                        required: true,
                    },
                    isCorrect: {
                        type: Boolean,
                        default: false,
                    }
                }
            ]
        }
    ]
}, { timestamps: true });

module.exports = mongoose.model("quiz", quizSchema);
