package library;

import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public class LoansImpl implements Loans {
    private List<Loan> loanList = newArrayList();

    @Override
    public void add(Item item, LocalDate dueDate, User user) {
        loanList.add(Loan.loan(item, dueDate, user));
    }

    @Override
    public List<Item> overDueItems(LocalDate today) {
        return loanList.stream().filter(l -> l.dueDate.isBefore(today)).map(l -> l.item).collect(toList());
    }

    @Override
    public List<Item> borrowedBy(User user) {
        return loanList.stream().filter(l -> l.user.equals(user)).map(l -> l.item).collect(toList());
    }

    @Override
    public boolean remove(Item item) {
        return loanList.remove(loanList.stream().filter(l -> l.item.equals(item)).findAny().get());
    }

    public List<Item> loanItems() {
        return loanList.stream().map(loanList -> loanList.item).collect(toList());
    }
}
