function (doc, meta) {

    // emit only artifacts with a color
    if (doc.categories.color) {
        var date = new Date(doc.timestamp);
        var day = date.getDate();
        var month = date.getMonth() + 1;
        var year = date.getFullYear();
        emit([doc.group.type + "/" + doc.group.name, year, month, day], [doc.timestamp, doc.categories.color]);
    }
}