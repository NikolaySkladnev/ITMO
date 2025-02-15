#pragma once

#include <cmath>

template< typename T >
struct matrix_t
{
	T data[16];
};

template< typename T >
struct vector3_t
{
	T x, y, z;
};

template< typename T >
class Quat
{
  public:
	Quat(T w, T x, T y, T z) : m_value{ x, y, z, w } {}

	Quat() : Quat(0, 0, 0, 0) {}

	Quat(T angle, bool degrees, const vector3_t< T >& vector)
	{
		if (!degrees)
		{
			angle *= ((T)M_PI / 180);
		}

		vector3_t< T > vector_norm = normalize_vector(vector);

		T sin = std::sin(angle / 2);
		T cos = std::cos(angle / 2);

		m_value[0] = vector_norm.x * sin;
		m_value[1] = vector_norm.y * sin;
		m_value[2] = vector_norm.z * sin;
		m_value[3] = cos;
	}
	const T* data() const { return m_value; }

	matrix_t< T > rotation_matrix() const
	{
		Quat< T > norm = *this;

		norm.normalize_quat();

		T x = norm.m_value[0];
		T y = norm.m_value[1];
		T z = norm.m_value[2];
		T w = norm.m_value[3];

		return {
			1 - 2 * (y * y + z * z),
			2 * (x * y + w * z),
			2 * (x * z - w * y),
			0,
			2 * (x * y - w * z),
			1 - 2 * (x * x + z * z),
			2 * (y * z + w * x),
			0,
			2 * (x * z + w * y),
			2 * (y * z - w * x),
			1 - 2 * (x * x + y * y),
			0,
			0,
			0,
			0,
			1
		};
	}

	matrix_t< T > matrix() const
	{
		T x = m_value[0];
		T y = m_value[1];
		T z = m_value[2];
		T w = m_value[3];

		return { w, -x, -y, -z, x, w, -z, y, y, z, w, -x, z, -y, x, w };
	}

	T angle() const { return 2 * std::acos(m_value[3]); }

	T angle(bool const angel_t) const
	{
		T result = angle();
		if (!angel_t)
		{
			return result * (180 / (T)M_PI);
		}
		return result;
	}

	vector3_t< T > apply(vector3_t< T > vector) const
	{
		Quat< T > normalized = *this;
		normalized.normalize_quat();
		Quat< T > vecQuat(0, vector.x, vector.y, vector.z);
		Quat< T > result = normalized * vecQuat * ~normalized;
		return { result.m_value[0], result.m_value[1], result.m_value[2] };
	}

	Quat< T >& operator+=(Quat< T > const arg)
	{
		for (int i = 0; i < 4; ++i)
		{
			m_value[i] += arg.m_value[i];
		}
		return *this;
	}

	Quat< T > operator+(Quat< T > const arg) const
	{
		Quat< T > result = *this;
		return result += arg;
	}

	Quat< T >& operator-=(Quat< T > const & arg)
	{
		for (int i = 0; i < 4; ++i)
		{
			m_value[i] -= arg.m_value[i];
		}
		return *this;
	}

	Quat< T > operator-(Quat< T > const arg) const
	{
		Quat< T > result = *this;
		return result -= arg;
	}

	Quat< T > operator*(Quat< T > const arg) const
	{
		T x = arg.m_value[0];
		T y = arg.m_value[1];
		T z = arg.m_value[2];
		T w = arg.m_value[3];

		T m_x = m_value[0];
		T m_y = m_value[1];
		T m_z = m_value[2];
		T m_w = m_value[3];

		return { m_w * w - m_x * x - m_y * y - m_z * z,
				 m_w * x + m_x * w + m_y * z - m_z * y,
				 m_w * y - m_x * z + m_y * w + m_z * x,
				 m_w * z + m_x * y - m_y * x + m_z * w };
	}

	Quat< T > operator*(vector3_t< T > const vector) const
	{
		return *this * Quat< T >(0, vector.x, vector.y, vector.z);
	}

	Quat< T > operator*(T const scalar) const
	{
		return Quat< T >(m_value[3] * scalar, m_value[0] * scalar, m_value[1] * scalar, m_value[2] * scalar);
	}

	Quat< T > operator~() const { return Quat< T >(m_value[3], -m_value[0], -m_value[1], -m_value[2]); }

	bool operator==(Quat< T > const arg) const { return std::equal(m_value, m_value + 4, arg.m_value); }

	bool operator!=(Quat< T > const arg) const { return !(*this == arg); }

	explicit operator T() const { return norm(); }

  private:
	T m_value[4];

	T norm() const
	{
		return std::sqrt(m_value[0] * m_value[0] + m_value[1] * m_value[1] + m_value[2] * m_value[2] + m_value[3] * m_value[3]);
	}

	void normalize_quat()
	{
		T normalized = norm();
		if (normalized != 0)
		{
			for (auto& element : m_value)
			{
				element /= normalized;
			}
		}
	}

	static vector3_t< T > normalize_vector(vector3_t< T > const vector)
	{
		T x = vector.x;
		T y = vector.y;
		T z = vector.z;
		T normalized = sqrt(x * x + y * y + z * z);
		if (normalized == 0)
			return vector;
		return { x / normalized, y / normalized, z / normalized };
	}
};
