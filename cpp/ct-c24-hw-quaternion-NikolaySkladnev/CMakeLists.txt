cmake_minimum_required(VERSION 3.26)
project(ct_c24_hw_quaternion_NikolaySkladnev)

set(CMAKE_CXX_STANDARD 17)

add_executable(ct_c24_hw_quaternion_NikolaySkladnev
        quat.hpp
        tests.cpp
)

add_subdirectory(googletest)

include_directories(${gtest_SOURCE_DIR}/include ${gtest_SOURCE_DIR})
target_link_libraries(ct_c24_hw_quaternion_NikolaySkladnev gtest gtest_main)