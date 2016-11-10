import React, { PropTypes } from 'react';

function Dashboard({ addSticky, updateSticky, removeSticky }) {
  return (
    <div className="dashboard">
      <button className="button" onClick={() => { addSticky(); }} >Add</button>
      <button className="button" onClick={() => { removeSticky(); }}>Remove</button>
      <button className="button" onClick={() => { updateSticky(); }}>Update</button>
    </div>
  );
}

Dashboard.propTypes = {
  addSticky: PropTypes.func,
  removeSticky: PropTypes.func,
  updateSticky: PropTypes.func
};

export default Dashboard;
