import React, { PropTypes } from 'react';

function Dashboard({ addCard, updateCard, removeCard, addOrga, removeOrga }) {
  return (
    <div className="dashboard">
      <button className="button" onClick={() => { addOrga(); }} >Add orga</button>
      <button className="button" onClick={() => { removeOrga(); }} >remove orga</button>
      <button className="button" onClick={() => { addCard(); }} >Add card</button>
      <button className="button" onClick={() => { removeCard(); }}>Remove card</button>
      <button className="button" onClick={() => { updateCard(); }}>Update card</button>
    </div>
  );
}

Dashboard.propTypes = {
  addCard: PropTypes.func,
  removeCard: PropTypes.func,
  updateCard: PropTypes.func,
  addOrga: PropTypes.func,
  removeOrga: PropTypes.func,
};

export default Dashboard;
