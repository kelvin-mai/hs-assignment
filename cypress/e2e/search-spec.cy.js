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
      url: "/api/patient?limit=10&offset=0&attr=patient%2Fcreated&dir=desc",
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
      url: "/api/patient?limit=10&offset=0&attr=patient%2Fcreated&dir=desc&patient/name=Ryan",
      method: "GET",
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
      url: "/api/patient?limit=10&offset=0&attr=patient%2Fcreated&dir=desc&patient/address=Street",
      method: "GET",
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
      url: "/api/patient?limit=10&offset=0&attr=patient%2Fcreated&dir=desc&patient/sex=male",
      method: "GET",
    }).as("patient-search");
    cy.get("[data-test-id=':patient/sex']").click();
    cy.get("li").contains("Male").click();
    cy.get("[data-test-id=patient-search]").click();
    cy.wait("@patient-search").then((intercept) => {
      const total = intercept.response.body.data[0].total;
      expect(total).eq(59);
    });
  });

  it("should have 6 woman results", () => {
    cy.intercept({
      url: "/api/patient?limit=10&offset=0&attr=patient%2Fcreated&dir=desc&patient/gender=woman",
      method: "GET",
    }).as("patient-search");
    cy.get("[data-test-id=':patient/gender']").click();
    cy.get("li").contains("Woman").click();
    cy.get("[data-test-id=patient-search]").click();
    cy.wait("@patient-search").then((intercept) => {
      const total = intercept.response.body.data[0].total;
      expect(total).eq(6);
    });
  });
});
