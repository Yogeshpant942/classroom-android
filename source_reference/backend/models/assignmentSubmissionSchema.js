const mongoose = require("mongoose");

const assignmentSubmissionSchema = new mongoose.Schema({
    assignment: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'assignment',
        required: true,
    },
    student: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'student',
        required: true,
    },
    content: {
        type: String, // student uploads text content
        required: true,
    },
    summary: {
        type: String,
    },
    plagiarizedFrom: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'student'
    }]
}, { timestamps: true });

module.exports = mongoose.model("assignmentSubmission", assignmentSubmissionSchema);
