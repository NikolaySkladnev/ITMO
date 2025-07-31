package info.kgeorgiy.ja.skladnev.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StudentDB implements StudentQuery {
    private static final Comparator<Student> FULL_ORDER = Comparator
            .comparing(Student::firstName)
            .thenComparing(Student::lastName)
            .thenComparing(Student::id);
    private static final BinaryOperator<String> MIN_STUDENT_NAME = BinaryOperator.minBy(Comparator.naturalOrder());

    @Override // :NOTE: toList всегда остался, сделайте промежуточную функцию
    public List<String> getFirstNames(List<Student> students) {
        return StudentDB.<String, List<String>>getStudentsBy(students)
                .apply(Student::firstName)
                .apply(toList());
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return StudentDB.<String, List<String>>getStudentsBy(students)
                .apply(Student::lastName)
                .apply(toList());
    }

    public List<GroupName> getGroupNames(List<Student> students) {
        return StudentDB.<GroupName, List<GroupName>>getStudentsBy(students)
                .apply(Student::groupName)
                .apply(toList());
    }


    @Override
    public List<String> getFullNames(List<Student> students) {
        return StudentDB.<String, List<String>>getStudentsBy(students)
                .apply(this::fullName)
                .apply(toList());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return StudentDB.<String, Set<String>>getStudentsBy(students)
                .apply(Student::firstName)
                .apply(toTreeSet());
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream().max(Student::compareTo).map(Student::firstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return StudentDB.<List<Student>>sortStudentsBy(students)
                .apply(Student::compareTo)
                .apply(toList());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return StudentDB.<List<Student>>sortStudentsBy(students)
                .apply(FULL_ORDER)
                .apply(toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return StudentDB.<List<Student>>findStudentsBy(students)
                .apply(FULL_ORDER)
                .apply(equalsBy(Student::firstName, name))
                .apply(toList());
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return StudentDB.<List<Student>>findStudentsBy(students)
                .apply(FULL_ORDER)
                .apply(equalsBy(Student::lastName, name))
                .apply(toList());
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return StudentDB.<List<Student>>findStudentsBy(students)
                .apply(FULL_ORDER)
                .apply(equalsBy(Student::groupName, group))
                .apply(toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return StudentDB.<Map<String, String>>findStudentsBy(students)
                .apply(FULL_ORDER)
                .apply(equalsBy(Student::groupName, group))
                .apply(Collectors.toMap(Student::lastName, Student::firstName, MIN_STUDENT_NAME));
    }

    private static <R, T> Function<Function<Student, R>, Function<Collector<? super R, ?, T>, T>> getStudentsBy(List<Student> students) {
        return mapper -> collector -> students.stream().map(mapper).collect(collector);
    }

    private static <T> Function<Comparator<? super Student>, Function<Collector<Student, ?, T>, T>> sortStudentsBy(Collection<Student> students) {
        return comparator -> collector -> students.stream().sorted(comparator).collect(collector);
    }

    private static <T> Function<Comparator<? super Student>, Function<Predicate<? super Student>, Function<Collector<Student, ?, T>, T>>> findStudentsBy(Collection<Student> students) {
        return comparator -> predicate -> collector -> students.stream().filter(predicate).sorted(comparator).collect(collector);
    }

    // :NOTE: можно объединить
    private <T> Predicate<Student> equalsBy(Function<Student, T> property, T value) {
        return student -> Objects.equals(property.apply(student), value);
    }

    private static <T> Collector<T, ?, List<T>> toList() {
        return Collectors.toList();
    }

    private static <T> Collector<T, ?, Set<T>> toTreeSet() {
        return Collectors.toCollection(TreeSet::new);
    }

    private String fullName(Student student) {
        return student.firstName() + " " + student.lastName();
    }
}
