const mongoose = require("mongoose");

const chunkSchema = new mongoose.Schema({
    chunkIndex: {
        type: Number,
        required: true
    },
    text: {
        type: String,
        required: true
    },
    embedding: {
        type: [Number],
        default: []
    }
});

const studyMaterialSchema = new mongoose.Schema({
    title: {
        type: String,
        required: true
    },
    content: {
        type: String,
        required: true
    },
    subject: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'subject'
    },
    sclass: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'sclass',
        required: true
    },
    school: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'admin',
        required: true
    },
    uploadedBy: {
        type: String,
        default: 'Teacher'
    },
    chunks: [chunkSchema],
    createdAt: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model("studyMaterial", studyMaterialSchema);
