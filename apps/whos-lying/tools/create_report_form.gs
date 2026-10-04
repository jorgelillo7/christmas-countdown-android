/**
 * Creates the "Report a word" Google Form for Who's lying?, its responses spreadsheet, and logs
 * the prefilled link the app needs.
 *
 * Usage and how to change it: see "Report-a-word form" in ../OPERATIONS.md. In short:
 * script.google.com → New project (or the existing one) → paste this file → Run `createReportForm` → authorize →
 * copy the "Prefilled" line from the execution log.
 */
function createReportForm() {
  const form = FormApp.create('¿Quién miente? · Reportar palabra');
  form.setDescription('Gracias por ayudar a mejorar el juego. Solo tienes que pulsar Enviar.');
  form.setConfirmationMessage('¡Gracias! Lo revisaremos. Ya puedes volver al juego.');
  form.setCollectEmail(false); // no personal data
  form.setLimitOneResponsePerUser(false); // true would force a Google sign-in
  form.setAllowResponseEdits(false);
  try {
    form.setRequireLogin(false); // only exists on Workspace accounts
  } catch (e) {
    // Personal accounts never require login.
  }

  const titles = ['Palabra', 'Parecida', 'Paquete', 'Idioma', 'Motivo'];
  const fields = titles.map((title) => form.addTextItem().setTitle(title));
  const comment = form.addParagraphTextItem().setTitle('Comentario');

  const sheet = SpreadsheetApp.create('¿Quién miente? · Reportes');
  form.setDestination(FormApp.DestinationType.SPREADSHEET, sheet.getId());

  // Placeholders the app replaces with the real values.
  const prefilled = form.createResponse();
  titles.forEach((title, i) => prefilled.withItemResponse(fields[i].createResponse(title.toUpperCase())));
  prefilled.withItemResponse(comment.createResponse('COMENTARIO'));

  Logger.log('Prefilled: ' + prefilled.toPrefilledUrl());
  Logger.log('Edit form: ' + form.getEditUrl());
  Logger.log('Responses: ' + sheet.getUrl());
}
