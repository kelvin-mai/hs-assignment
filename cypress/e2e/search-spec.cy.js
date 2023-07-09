const url = "http://localhost:8080";

describe("patient search e2e spec", () => {
  before(() => {
    return cy.task("initTestData", "cypress/fill_database.sql");
  });

  beforeEach(() => {
    cy.visit(url);
    cy.get("body").type("{ctrl}h", { force: true });
    cy.get("[data-test-id=open-search-filter]").click();
  });

  it("should have 100 results without filters", () => {
    cy.intercept({
      pathname: "/api/patient",
      method: "GET",
    }).as("patient-search");
    cy.get("[data-test-id=patient-search]").click();
    cy.wait("@patient-search").then((intercept) => {
      const total = intercept.response.body.data[0].total;
      expect(total).eq(100);
    });
  });

  it("should have 3 Ryan results", () => {
    cy.intercept({
      pathname: "/api/patient",
      method: "GET",
      query: {
        "patient/name": "Ryan",
      },
    }).as("patient-search");
    cy.get("[data-test-id=':patient/name']").type("Ryan", { delay: 120 });
    cy.get("[data-test-id=patient-search]").click();
    cy.wait("@patient-search").then((intercept) => {
      const total = intercept.response.body.data[0].total;
      expect(total).eq(3);
    });
  });

  it("should have 14 Street results", () => {
    cy.intercept({
      pathname: "/api/patient",
      method: "GET",
      query: {
        "patient/address": "Street",
      },
    }).as("patient-search");
    cy.get("[data-test-id=':patient/address']").type("Street", { delay: 120 });
    cy.get("[data-test-id=patient-search]").click();
    cy.wait("@patient-search").then((intercept) => {
      const total = intercept.response.body.data[0].total;
      expect(total).eq(14);
    });
  });

  it("should have 59 male results", () => {
    cy.intercept({
      pathname: "/api/patient",
      method: "GET",
      query: {
        "patient/sex": "male",
      },
    }).as("patient-search");
    cy.get("[data-test-id=':patient/sex']").click();
    cy.get("li").contains("Male").click();
    cy.get("[data-test-id=patient-search]").click();
    cy.wait("@patient-search").then((intercept) => {
      const total = intercept.response.body.data[0].total;
      expect(total).eq(59);
    });
  });

  it("should have 35 woman results", () => {
    cy.intercept({
      pathname: "/api/patient",
      method: "GET",
      query: {
        "patient/gender": "woman",
      },
    }).as("patient-search");
    cy.get("[data-test-id=':patient/gender']").click();
    cy.get("li").contains("Woman").click();
    cy.get("[data-test-id=patient-search]").click();
    cy.wait("@patient-search").then((intercept) => {
      const total = intercept.response.body.data[0].total;
      expect(total).eq(35);
    });
  });

  it("should have age results", () => {
    cy.intercept({
      pathname: "/api/patient",
      method: "GET",
      query: {
        "patient/age": "under 5",
      },
    }).as("patient-search");
    cy.get("[data-test-id=':patient/age']").click();
    cy.get("li").contains("Under 5").click();
    cy.get("[data-test-id=patient-search]").click();
    cy.wait("@patient-search").then((intercept) => {
      const total = intercept.response.body.data[0].total;
      expect(total).eq(1);
    });

    cy.intercept({
      pathname: "/api/patient",
      method: "GET",
      query: {
        "patient/age": "65 and older",
      },
    }).as("patient-search");
    cy.get("[data-test-id=':patient/age']").click();
    cy.get("li").contains("65 and older").click();
    cy.get("[data-test-id=patient-search]").click();
    cy.wait("@patient-search").then((intercept) => {
      const total = intercept.response.body.data[0].total;
      expect(total).eq(35);
    });

    cy.intercept({
      pathname: "/api/patient",
      method: "GET",
      query: {
        "patient/age": "18 - 64",
      },
    }).as("patient-search");
    cy.get("[data-test-id=':patient/age']").click();
    cy.get("li").contains("18 - 64").click();
    cy.get("[data-test-id=patient-search]").click();
    cy.wait("@patient-search").then((intercept) => {
      const total = intercept.response.body.data[0].total;
      expect(total).eq(51);
    });
  });
});
