import React from 'react';

function DayAndNight() {
  return (
    <div className="toggle toggle--daynight">
      <input type="checkbox" id="toggle--daynight" className="toggle--checkbox"/>
      <label className="toggle--btn" htmlFor="toggle--daynight">
        <span className="toggle--feature"/>
      </label>
    </div>
  );
}

export default DayAndNight;
