const dayjs = require("dayjs");

const url = "http://localhost:8080";

describe("patient crud e2e spec", () => {
  before(() => {
    return cy.task("initTestData");
  });

  beforeEach(() => {
    cy.visit(url);
    cy.get("body").type("{ctrl}h", { force: true });
  });

  it("should create new patient from empty table", () => {
    cy.get("[data-test-id=empty-patients-create]").should("exist").click();
    cy.get("[data-test-id=patient-form-title]").should(
      "contain",
      "New Patient"
    );

    // validation
    cy.get("button[type=submit]").click();
    cy.get("[data-test-id=':patient/name'] p.Mui-error").should("exist");
    cy.get("[data-test-id=':patient/sex'] p.Mui-error").should("exist");
    cy.get("[data-test-id=':patient/dob'] label.Mui-error").should("exist");

    // after validation
    cy.get("[data-test-id=':patient/name']").type("Test", { delay: 120 });
    cy.get("[data-test-id=':patient/sex']").click();
    cy.get("li").contains("Male").click();
    cy.get("[aria-current=date]").click();
    cy.get("button[type=submit]").click();
    cy.get("[data-test-id=snackbar]")
      .should("exist")
      .contains("Patient data successfully submitted.");

    cy.contains("td", "Test").should("exist");
    cy.contains("td", "male").should("exist");
    cy.get("[data-test-id=empty-patients-create]").should("not.exist");
    cy.contains("td", dayjs().format("YYYY-MM-DD")).should("exist");
  });

  it("should create new patient from navbar", () => {
    cy.get("[data-test-id=open-navmenu]").click();
    cy.get("[data-test-id=':app.router/new-patient']").click();

    cy.get("[data-test-id=':patient/name']").type("Spec", { delay: 120 });
    cy.get("[data-test-id=':patient/sex']").click();
    cy.get("li").contains("Female").click();
    cy.get("[aria-current=date]").click();
    cy.get("button[type=submit]").click();
    cy.get("[data-test-id=snackbar]")
      .should("exist")
      .contains("Patient data successfully submitted.");

    cy.contains("td", "Spec").should("exist");
    cy.contains("td", "female").should("exist");
    cy.contains("td", dayjs().format("YYYY-MM-DD")).should("exist").click();
  });

  it("should update and delete patient", () => {
    cy.contains("td", "Test").should("exist").click();
    cy.get("[data-test-id=patient-form-title]").should("contain", "Patient");
    cy.get("[data-test-id=':patient/name'] input").should("have.value", "Test");
    cy.get("[data-test-id=':patient/name'] input").should("be.disabled");
    cy.get("[data-test-id=edit-patient]").should("exist").click();
    cy.get("[data-test-id=':patient/name'] input").should("be.enabled");
    cy.get("[data-test-id=':patient/address']").type("123 Fake St", {
      delay: 120,
    });
    cy.get("button[type=submit]").click();
    cy.get("[data-test-id=snackbar]")
      .should("exist")
      .contains("Patient data successfully submitted.");

    cy.contains("td", "Test").should("exist");
    cy.contains("td", "male").should("exist");
    cy.contains("td", "123 Fake St").should("exist").click();
    cy.get("[data-test-id=delete-patient]").should("exist").click();
    cy.get("[data-test-id=snackbar]")
      .should("exist")
      .contains("Patient data successfully deleted.");
  });

  it("should clear form data on navigation", () => {
    cy.contains("td", "Spec").should("exist").click();
    cy.get("[data-test-id=patient-form-title]").should("contain", "Patient");
    cy.get("[data-test-id=':patient/name'] input").should("have.value", "Spec");
    cy.get("[data-test-id=':patient/name'] input").should("be.disabled");
    cy.get("[data-test-id=edit-patient]").should("exist");
    cy.get("[data-test-id=delete-patient]").should("exist");

    cy.get("[data-test-id=open-navmenu]").click();
    cy.get("[data-test-id=':app.router/new-patient']").click();
    cy.get("[data-test-id=patient-form-title]").should(
      "contain",
      "New Patient"
    );
    cy.get("[data-test-id=':patient/name'] input").should(
      "not.have.value",
      "Spec"
    );
    cy.get("[data-test-id=':patient/name'] input").should("be.enabled");
    cy.get("[data-test-id=edit-patient]").should("not.exist");
    cy.get("[data-test-id=delete-patient]").should("not.exist");
  });
});
