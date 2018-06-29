#ifndef SHAPE_H_
#define SHAPE_H_

#include <string>
#include <iosfwd>

class Shape {
public:
	virtual ~Shape();

	virtual unsigned int pegs() const =0;
	virtual double ropes() const =0;
	virtual double seeds() const =0;
	virtual std::string name() const =0;

	static constexpr double seedRatio = 0.1;
	static const double PI;
};

std::ostream& operator<<(std::ostream& os, Shape const& sh);
#endif /* SHAPE_H_ */
