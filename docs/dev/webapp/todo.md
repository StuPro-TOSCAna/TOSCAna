# TODO

The web app is still work and progress, has some weaknesses and missing features.

Here is a list of things that can still be done in no specific order:

- Currently accessing any using a link does not work.
  If a user refreshes the page, or accesses the web app with a link like TODO he will be redirected to the start page.
- At the moment most of the controllers contain a lot of logic that can be extracted, f.ex. the TransformationInputsController contains the whole validation logic.
- There are some UI bugs and weaknesses, on the most buttons the hover effect is missing, also there are sometimes resizing bugs where content is jumping around.
- The log currently handles transformation-view and csars-view log using an if/else this is not very nice, there might be a better idea to resolve this.
- In the transformation-view component the input and output tabs are part of the components code, they might be extracted in a extra template or component.
- The transformation-view and csar-view have nearly the same functionality, although they are two different components, there might be a better solution to reduce code to reduce code.
- Angular allows to do a lot of logic in the HTML files, currently this renders the code very unclear.
  Refactoring the code that there is a clear separation of UI and logic is really necessary.
