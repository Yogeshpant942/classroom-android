const mongoose = require("mongoose");

const labGroupSchema = new mongoose.Schema({
    groupName: {
        type: String,
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
    students: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'student',
    }]
}, { timestamps: true });

module.exports = mongoose.model("labGroup", labGroupSchema);
