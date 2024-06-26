//
// Created by Yancey on 2024/2/21.
//

#pragma once

#ifndef CHELPER_STRUCTUREBUILDER_H
#define CHELPER_STRUCTUREBUILDER_H

#include "pch.h"

namespace CHelper {

    class StructureBuilder {
    private:
        std::string structure;

    public:
        bool isDirty = false;

        StructureBuilder &appendUnknown(bool isMustHave);

        StructureBuilder &appendSymbol(char ch);

        StructureBuilder &append(const std::string &str);

        StructureBuilder &appendWhiteSpace();

        StructureBuilder &appendLeftBracket(bool isMustHave);

        StructureBuilder &appendRightBracket(bool isMustHave);

        StructureBuilder &append(bool isMustHave, const std::string &str);

        std::string build();
    };

}// namespace CHelper

#endif//CHELPER_STRUCTUREBUILDER_H
