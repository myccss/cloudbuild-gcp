Behaviour.specify("[data-type='gcloud-provision']", 'gcloud-provision', -99, function(e) {
  e.addEventListener("click", function (event) {
    event.preventDefault();
    event.stopPropagation();
    const form = document.getElementById(e.dataset.form);
    form.querySelector("[name='configuration']").value = e.dataset.configuration;
    buildFormTree(form);

    if (typeof form.requestSubmit == "function") {
      form.requestSubmit();
      return;
    }

    form.querySelector("button[type='submit']").click();
  });
});
