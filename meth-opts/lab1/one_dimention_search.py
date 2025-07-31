import time

import numpy as np
import math
import matplotlib.pyplot as plt
from abc import ABC, abstractmethod
from gradient_descent import GradientDescent, Function

class MinimizationMethod(ABC):
    def __init__(self, epsilon, f, name="Optimization"):
        self.epsilon = epsilon
        self.f = f
        self.name = name
        self.trajectory = []

    @abstractmethod
    def calc(self, a, b):
        pass

    def plot_trajectory(self, interval):
        plt.figure(figsize=(12, 6))

        # График функции
        x_vals = np.linspace(interval[0] - 1, interval[1] + 1, 400)
        y_vals = [self.f(x) for x in x_vals]
        plt.plot(x_vals, y_vals, color='b')

        # Траектория оптимизации
        trajectory_y = [self.f(x) for x in self.trajectory]
        plt.scatter(self.trajectory, trajectory_y, color='r', s=100, label=self.name)
        plt.plot(self.trajectory, trajectory_y, 'r--', alpha=0.5)

        # Нумерация точек
        for i, (x, y) in enumerate(zip(self.trajectory, trajectory_y)):
            plt.annotate(f'{i + 1}', (x, y), textcoords="offset points",
                         xytext=(0, 10), ha='center', fontsize=9)

        # Минимум
        final_x = self.trajectory[-1]
        final_y = self.f(final_x)
        plt.scatter([final_x], [final_y], color='g', s=150,
                    label=f"Minimum: x={final_x:.4f}\nf(x)={final_y:.4f}")

        plt.title(f"Function and Optimization trajectory\n{self.name}")
        plt.xlabel("x")
        plt.ylabel("f(x)")
        plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
        plt.grid(True)
        plt.tight_layout()
        plt.show()


class DichotomyMethod(MinimizationMethod):
    def __init__(self, epsilon, f):
        super().__init__(epsilon, f, "Dichotomy Method")

    def calc(self, a, b):
        delta = self.epsilon / 2
        while b - a > delta:
            c = (a + b) / 2
            if self.f(c - delta) < self.f(c + delta):
                b = c
            else:
                a = c
            self.trajectory.append((a + b) / 2)

        return (a + b) / 2


class GoldenSectionMethod(MinimizationMethod):
    def __init__(self, epsilon, f):
        super().__init__(epsilon, f, "Golden Section Method")
        self.phi = (math.sqrt(5) - 1) / 2  # ≈ 0.618

    def calc(self, a, b):
        delta = self.epsilon / 2
        alpha = (3 - math.sqrt(5)) / 2
        beta = (math.sqrt(5) - 1) / 2

        x1 = a + alpha * (b - a)
        x2 = a + beta * (b - a)
        f1 = self.f(x1)
        f2 = self.f(x2)

        while abs(b - a) > delta:
            if f1 > f2:
                a = x1
                x1 = x2
                f1 = f2
                x2 = a + beta * (b - a)
                f2 = self.f(x2)
            else:
                b = x2
                x2 = x1
                f2 = f1
                x1 = a + alpha * (b - a)
                f1 = self.f(x1)

            self.trajectory.append((a + b) / 2)

        return (a + b) / 2

class GradientDescentWithLineSearch(GradientDescent):
    def __init__(self, line_search_method, name, max_iter=50000):
        super().__init__(name=f"Gradient Descent with {name}", max_iter=max_iter)
        self.line_search_method = line_search_method

    def schedule(self, k):
        # Не используется, так как шаг выбирается адаптивно через одномерную оптимизацию
        return 1.0

    def optimize(self, func, grad_func, x0, eps=1e-6 ,apply_min=False, minimum=None):
        x = np.array(x0, dtype=float)
        trajectory = [x.copy()]

        start_time = time.time()
        for k in range(self.max_iter):
            grad = grad_func(x)


            if np.linalg.norm(grad) < self.line_search_method.epsilon:
                break

            if apply_min and minimum is not None and abs(func(x) - minimum) < self.line_search_method.epsilon:
                break

            # Функция одномерного поиска вдоль направления антиградиента
            def phi(alpha):
                return func(x - alpha * grad)

            # Обновляем функцию и очищаем траекторию (если есть)
            self.line_search_method.f = phi
            if hasattr(self.line_search_method, 'trajectory'):
                self.line_search_method.trajectory = []

            alpha_opt = self.line_search_method.calc(0, 1)

            x_new = x - alpha_opt * grad

            if np.any(np.isnan(x_new)) or np.any(np.isinf(x_new)):
                print(f"NaN or Inf encountered in new point at iteration {k}. Stopping optimization.")
                break

            x = x_new
            trajectory.append(x.copy())

        end_time = time.time()
        time_taken = end_time - start_time
        num_iterations = len(trajectory) - 1
        return x, np.array(trajectory), num_iterations, time_taken