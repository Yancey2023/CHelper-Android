cmake_minimum_required(VERSION 3.22.1)
project(CHelper)

# using c++ 17
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ThirdParty: hedley
include_directories(include/CHelper-Core/include/hedley/include)

# ThirdParty: json
include_directories(include/CHelper-Core/include/json/include)

# ThirdParty: fmt
add_subdirectory(include/CHelper-Core/include/fmt)
link_libraries(fmt::fmt)

# Sources
file(GLOB_RECURSE SOURCE_FILE include/CHelper-Core/src/chelper/*.h include/CHelper-Core/src/chelper/*.cpp)
include_directories(include/CHelper-Core/include/pch ${CMAKE_BINARY_DIR}/include)

# Android Shared Libary
add_library(${CMAKE_PROJECT_NAME} SHARED
        include/CHelper-Core/src/android/AndroidNative.cpp
        ${SOURCE_FILE}
)
target_precompile_headers(${CMAKE_PROJECT_NAME} PRIVATE include/CHelper-Core/include/pch/pch.h)
target_link_libraries(${CMAKE_PROJECT_NAME} PRIVATE android log)
target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE CHelperAndroid=true)
