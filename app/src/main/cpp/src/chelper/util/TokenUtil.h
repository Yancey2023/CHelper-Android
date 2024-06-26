//
// Created by Yancey on 2024/2/22.
//

#pragma once

#ifndef CHELPER_TOKENUTIL_H
#define CHELPER_TOKENUTIL_H

#include "../lexer/Token.h"

namespace CHelper::TokenUtil {

    size_t getIndex(const VectorView<Token> &tokens, size_t tokenIndex);

    size_t getStartIndex(const VectorView<Token> &tokens);

    size_t getEndIndex(const VectorView<Token> &tokens);

    std::string toString(const VectorView<Token> &tokens);

}// namespace CHelper::TokenUtil

#endif//CHELPER_TOKENUTIL_H
