Feature: Process monthly statement end to end

  Scenario: Bank API and outbound API both succeed
    Given the bank API returns the following transactions for account "acct-1" and month "2026-07":
      | date       | amount  | description | type   |
      | 2026-07-01 | 2000.00 | Salary      | CREDIT |
      | 2026-07-05 | -100.00 | Groceries   | DEBIT  |
    And the outbound API is ready to accept a monthly balance
    When I request statement processing for account "acct-1" and month "2026-07"
    Then the response status should be 200
    And the computed balance should be:
      | totalIncome   | 2000.00 |
      | totalSpending | 100.00  |
      | balance       | 1900.00 |
    And the outbound API should have received a balance of 1900.00

  Scenario: Bank API is unavailable
    Given the bank API is down for account "acct-1" and month "2026-07"
    When I request statement processing for account "acct-1" and month "2026-07"
    Then the response status should be 502