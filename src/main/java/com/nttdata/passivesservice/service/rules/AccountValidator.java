package com.nttdata.passivesservice.service.rules;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import com.nttdata.passivesservice.common.AccountType;
import com.nttdata.passivesservice.service.CustomerType;

public interface AccountValidator extends Function<AccountRule, Set<Rules>> {

  Predicate<AccountRule> isFixedTermAccount =
      ar -> ar.getAccount().getType() == AccountType.FIXED_TERM;
  Predicate<AccountRule> isCurrentAccount = ar -> ar.getAccount().getType() == AccountType.CURRENT;
  Predicate<AccountRule> isSavingAccount = ar -> ar.getAccount().getType() == AccountType.SAVING;

  Predicate<AccountRule> isNatural = ar -> ar.getCustomerType() == CustomerType.NATURAL;
  Predicate<AccountRule> isCompany = ar -> ar.getCustomerType() == CustomerType.COMPANY;

  AccountValidator empty = ar -> Collections.emptySet();

  AccountValidator companyHasNotFixedTermAccount =
      rule(isFixedTermAccount.and(isCompany), Rules.COMPANY_CAN_NOT_HAVE_FIXED_TERM_ACCOUNT);

  AccountValidator companyHasNotSavingAccount =
      rule(isSavingAccount.and(isCompany), Rules.COMPANY_CAN_NOT_HAVE_SAVING_ACCOUNT);

  static AccountValidator rule(final Predicate<AccountRule> predicate, final Rules rule) {
    return ar -> predicate.test(ar) ? Collections.singleton(rule) : Collections.emptySet();
  }

  default AccountValidator and(final AccountValidator other) {
    return ar -> {
      final Set<Rules> left = this.apply(ar);
      final Set<Rules> right = other.apply(ar);

      final Set<Rules> merged = new HashSet<>(left);
      merged.addAll(right);

      return merged;
    };
  }

}
