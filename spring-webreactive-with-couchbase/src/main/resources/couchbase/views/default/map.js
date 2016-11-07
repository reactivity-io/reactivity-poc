function (doc, meta) {
    emit([doc.timestamp, meta.id], doc);
}