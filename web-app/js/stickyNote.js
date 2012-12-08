function addStickyNote(id) {
  document.getElementById('noteId').value=id;
  document.location.hash='#stickyNoteDialog';
}

function submitStickyNote() {
  var noteId = document.getElementById('noteId').value;
  var noteTitle = document.getElementById('noteTitle').value;
  var noteSummary = document.getElementById('noteSummary').value;
  var noteMessage = document.getElementById('noteMessage').value;
  
  // todo : submit note to server

  clearStickyNote();
}

function clearStickyNote() {
  document.getElementById('noteId').value='';
  document.getElementById('noteTitle').value='';
  document.getElementById('noteSummary').value='';
  document.getElementById('noteMessage').value='';
}
