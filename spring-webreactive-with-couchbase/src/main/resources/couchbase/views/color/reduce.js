// Count each color
function (keys, values, rereduce) {
    var obj = {};
    var max = 0;

    for (i in values) {
        var row = values[i];

        if (!obj[row[1]]) {
            obj[row[1]] = 1;
        } else {
            obj[row[1]] = obj[row[1]] + 1;
        }

        max = Math.max(max, row[0]);
    }

    return { count : obj, lastTimestamp : max };
}