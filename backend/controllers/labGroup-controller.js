const LabGroup = require('../models/labGroupSchema');
const Student = require('../models/studentSchema');

const createLabGroup = async (req, res) => {
    try {
        const labGroup = new LabGroup(req.body);
        const result = await labGroup.save();

        // Update all students in this group to have the labGroup reference
        if (req.body.students && req.body.students.length > 0) {
            await Student.updateMany(
                { _id: { $in: req.body.students } },
                { $set: { labGroup: result._id } }
            );
        }

        res.send(result);
    } catch (err) {
        res.status(500).json(err);
    }
};

const getLabGroupsByClass = async (req, res) => {
    try {
        const labGroups = await LabGroup.find({ sclassName: req.params.id }).populate('students', 'name rollNum');
        res.send(labGroups);
    } catch (err) {
        res.status(500).json(err);
    }
};

const assignStudentToLabGroup = async (req, res) => {
    try {
        const { labGroupId, studentId } = req.body;

        const labGroup = await LabGroup.findById(labGroupId);
        if (!labGroup) {
            return res.send({ message: "Lab group not found" });
        }

        if (!labGroup.students.includes(studentId)) {
            labGroup.students.push(studentId);
            await labGroup.save();

            // Update student
            await Student.findByIdAndUpdate(studentId, { $set: { labGroup: labGroupId } });
        }

        res.send({ message: "Student assigned successfully" });
    } catch (err) {
        res.status(500).json(err);
    }
};

module.exports = {
    createLabGroup,
    getLabGroupsByClass,
    assignStudentToLabGroup
};
