cmake_minimum_required(VERSION 3.22.1)
project(CHelper)

file(GLOB_RECURSE SOURCE_H src/*.h)
file(GLOB_RECURSE SOURCE_CPP src/*.cpp)
add_library(${CMAKE_PROJECT_NAME} SHARED ${SOURCE_H} ${SOURCE_CPP})

target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC include)
target_precompile_headers(${CMAKE_PROJECT_NAME} PRIVATE include/pch.h)

target_link_libraries(${CMAKE_PROJECT_NAME} android log)